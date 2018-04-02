package io.block16.ethlistener.listener;

import io.block16.ethlistener.domain.EthereumEvent;
import io.block16.ethlistener.domain.EthereumTransactionType;
import io.block16.ethlistener.domain.jpa.EthereumAddress;
import io.block16.ethlistener.domain.jpa.EthereumContract;
import io.block16.ethlistener.domain.jpa.EthereumTransaction;

import io.block16.ethlistener.domain.jpa.TokenTransaction;
import io.block16.ethlistener.service.EthereumAddressService;
import io.block16.ethlistener.service.EthereumContractService;
import io.block16.ethlistener.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class DatabaseBuilderListener {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private EthereumAddressService ethereumAddressService;
    private TransactionService transactionService;
    private EthereumContractService ethereumContractService;
    private final BigInteger byzantine;
    private final BigInteger etherToWei;

    public DatabaseBuilderListener(
            final EthereumAddressService ethereumAddressService,
            final EthereumContractService ethereumContractService
            ) {
        this.ethereumAddressService = ethereumAddressService;
        this.transactionService = transactionService;
        this.ethereumContractService = ethereumContractService;
        byzantine = BigInteger.valueOf(4370000);
        etherToWei = new BigInteger("1000000000000000000");
    }

    public void onBlock(EthBlock block,
                        List<EthBlock> uncles,
                        List<EthBlock.TransactionObject> transactionObjects,
                        List<TransactionReceipt> transactionReceipts) {
        this.processBlock(block, uncles, transactionObjects, transactionReceipts);
    }

    public void onTransaction(EthBlock block, EthBlock.TransactionObject transactionObject, TransactionReceipt transactionReceipt) {
        this.processTransactions(block, transactionObject);
        this.processReceipts(block, transactionObject, transactionReceipt);
    }

    void processBlock(EthBlock ethBlock,
                      List<EthBlock> unclesList,
                      List<EthBlock.TransactionObject> transactionObjects,
                      List<TransactionReceipt> transactionReceipts) {
        // 4370000 block reward 5 -> 3
        // (Uncle Number + 8 - blocknumber) * reward / 8, mined block is an uncle :D
        // gas + reward + 1/32 * blockReward per uncle (3 or 5)
        EthBlock.Block block = ethBlock.getBlock();
        int reward = this.byzantine.compareTo(ethBlock.getBlock().getNumber()) >= 0 ? 5 : 3;

        String miner = block.getMiner().substring(2);
        BigInteger minerReward = BigInteger.valueOf(reward).multiply(etherToWei);

        BigInteger transactionGas = BigInteger.ZERO;
        for(int i = 0; i < transactionReceipts.size(); i++) {
            transactionGas = transactionGas.add(transactionReceipts.get(i).getGasUsed().multiply(transactionObjects.get(i).getGasPrice()));
        }

        BigInteger inclusionReward = BigInteger.valueOf(unclesList.size())
                .multiply(minerReward)
                .divide(BigInteger.valueOf(32));

        for(EthBlock uncle: unclesList) {
            // TODO: this can be optimized
            BigInteger blockDiff = uncle.getBlock().getNumber().add(BigInteger.valueOf(8)).subtract(block.getNumber());
            BigInteger rewardDivWei = minerReward.divide(BigInteger.valueOf(8));
            BigInteger uncleReward = blockDiff.multiply(rewardDivWei);
            EthereumAddress uncleAddress =
                    this.ethereumAddressService.getOrCreateByAddress(uncle.getBlock().getMiner().substring(2).toLowerCase());
            EthereumTransaction uncleTransaction = new EthereumTransaction();
            uncleTransaction.setTransactionType(EthereumTransactionType.UNCLE_REWARD);
            uncleTransaction.setToAddress(uncleAddress);
            uncleTransaction.setTime(new Timestamp(uncle.getBlock().getTimestamp().longValueExact() * 1000));
            uncleTransaction.setValue(uncleReward.toString());
            this.transactionService.save(uncleTransaction);
        }

        BigInteger totalReward = minerReward.add(transactionGas).add(inclusionReward);

        EthereumAddress minerAddress = this.ethereumAddressService.getOrCreateByAddress(miner.toLowerCase());
        EthereumTransaction minerTransaction = new EthereumTransaction();
        minerTransaction.setValue(totalReward.toString());
        minerTransaction.setTime(new Timestamp(ethBlock.getBlock().getTimestamp().longValueExact() * 1000));
        minerTransaction.setToAddress(minerAddress);
        minerTransaction.setTransactionType(EthereumTransactionType.MINING_REWARD);
        this.transactionService.save(minerTransaction);

        LOGGER.info("Miner Reward: {}, gas: {}, inclusion reward: {}, total: {}", minerReward, transactionGas, inclusionReward, totalReward);
    }

    /**
     * Processes transactions within a block + block rewards / uncle rewards
     * @param ethBlock
     * @param transactionObject
     */
    void processTransactions(EthBlock ethBlock, EthBlock.TransactionObject transactionObject) {
        String sender = transactionObject.getFrom().substring(2, transactionObject.getFrom().length()).toLowerCase();
        String receiver = transactionObject.getTo();

        EthereumTransaction ethereumTransaction = new EthereumTransaction();
        ethereumTransaction.setValue(transactionObject.getValue().toString());
        ethereumTransaction.setTransactionHash(transactionObject.getHash().startsWith("0x") ? transactionObject.getHash().substring(2) : transactionObject.getHash());

        // Sender address
        ethereumTransaction.setFromAddress(this.ethereumAddressService.getOrCreateByAddress(sender));
        ethereumTransaction.setTime(new Timestamp(ethBlock.getBlock().getTimestamp().longValueExact() * 1000));

        // Not interested in this
        if (transactionObject.getValue().compareTo(BigInteger.valueOf(0)) <= 0) {
            // Token transaction, creates or contract interaction
            return;
        }

        if (receiver == null) {
            ethereumTransaction.setToAddress(null);
            ethereumTransaction.setTransactionType(EthereumTransactionType.CONTRACT_CREATION);
            EthereumContract ethereumContract = this.ethereumContractService.getOrCreateContract(transactionObject.getCreates().substring(2));
            ethereumTransaction.setEthereumContract(ethereumContract);
            this.transactionService.save(ethereumTransaction);
            return;
        }

        receiver = receiver.substring(2, receiver.length()).toLowerCase();
        ethereumTransaction.setTransactionType(EthereumTransactionType.NORMAL);
        ethereumTransaction.setToAddress(this.ethereumAddressService.getOrCreateByAddress(receiver));

        this.transactionService.save(ethereumTransaction);
    }

    /**
     * Looks for token transactions in ethereum logs
     */
    void processReceipts(EthBlock ethBlock, EthBlock.TransactionObject transactionObject, TransactionReceipt transactionReceipt) {
        List<TokenTransaction> tokenTransactions = new LinkedList<>();

        transactionReceipt.getLogs().forEach((log -> {
            EthereumEvent ethereumEvent = new EthereumEvent(log.getAddress(), log.getTopics(), log.getData());
            if(ethereumEvent.isTokenTransfer()) {
                String sender = ethereumEvent.getTopics().wordToAddress(1);
                String receiver = ethereumEvent.getTopics().wordToAddress(2);
                BigInteger amount = new BigInteger(ethereumEvent.getData(), 16);
                String contractAddress = log.getAddress().substring(2);

                EthereumContract ethereumContract = this.ethereumContractService.getOrCreateContract(contractAddress);

                // Outbound token transactions
                TokenTransaction tokenTransaction = new TokenTransaction();
                tokenTransaction.setEthereumContract(ethereumContract);
                tokenTransaction.setAmount(amount.toString());
                tokenTransaction.setTime(new Timestamp(ethBlock.getBlock().getTimestamp().longValueExact() * 1000));
                tokenTransaction.setFromAddress(this.ethereumAddressService.getOrCreateByAddress(sender));
                tokenTransaction.setToAddress(this.ethereumAddressService.getOrCreateByAddress(receiver));
                tokenTransaction.setTransactionHash(transactionObject.getHash().startsWith("0x") ? transactionObject.getHash().substring(2) : transactionObject.getHash());
                tokenTransactions.add(tokenTransaction);
            }
        }));
    }
}
