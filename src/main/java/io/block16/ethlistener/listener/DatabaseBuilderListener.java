package io.block16.ethlistener.listener;

import io.block16.ethlistener.domain.EthereumEvent;
import io.block16.ethlistener.domain.EthereumTransactionType;
import io.block16.ethlistener.domain.jpa.EthereumAddress;
import io.block16.ethlistener.domain.jpa.EthereumTransaction;

import io.block16.ethlistener.service.EthereumAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DatabaseBuilderListener {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private EthereumAddressService ethereumAddressService;
    private final BigInteger byzantine = BigInteger.valueOf(4370000);
    private final BigInteger etherToWei = new BigInteger("1000000000000000000");

    public DatabaseBuilderListener(final EthereumAddressService ethereumAddressService) {
        this.ethereumAddressService = ethereumAddressService;
    }

    public List<EthereumTransaction> onBlock(EthBlock block,
                        List<EthBlock> uncles,
                        List<EthBlock.TransactionObject> transactionObjects,
                        List<TransactionReceipt> transactionReceipts) {
        return this.processBlock(block, uncles, transactionObjects, transactionReceipts);
    }

    public List<EthereumTransaction> onTransaction(EthBlock block, EthBlock.TransactionObject transactionObject, TransactionReceipt transactionReceipt) {
        return this.processReceipts(block, transactionObject, transactionReceipt);
    }

    List<EthereumTransaction> processBlock(EthBlock ethBlock,
                      List<EthBlock> unclesList,
                      List<EthBlock.TransactionObject> transactionObjects,
                      List<TransactionReceipt> transactionReceipts) {
        List<EthereumTransaction> ethereumTransactions = new ArrayList<>(3);

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
            // this can be optimized
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

            ethereumTransactions.add(uncleTransaction);
        }

        BigInteger totalReward = minerReward.add(transactionGas).add(inclusionReward);

        EthereumAddress minerAddress = this.ethereumAddressService.getOrCreateByAddress(miner.toLowerCase());
        EthereumTransaction minerTransaction = new EthereumTransaction();
        minerTransaction.setValue(totalReward.toString());
        minerTransaction.setTime(new Timestamp(ethBlock.getBlock().getTimestamp().longValueExact() * 1000));
        minerTransaction.setToAddress(minerAddress);
        minerTransaction.setTransactionType(EthereumTransactionType.MINING_REWARD);
        ethereumTransactions.add(minerTransaction);

        LOGGER.info("Miner Reward: {}, gas: {}, inclusion reward: {}, total: {}", minerReward, transactionGas, inclusionReward, totalReward);
        return ethereumTransactions;
    }

    /**
     * Looks for token transactions in ethereum logs
     */
    List<EthereumTransaction> processReceipts(EthBlock ethBlock, EthBlock.TransactionObject transactionObject, TransactionReceipt transactionReceipt) {
        List<EthereumTransaction> transactions = new LinkedList<>();

        // Transaction processing without token
        String sender = transactionObject.getFrom().substring(2, transactionObject.getFrom().length()).toLowerCase();
        String receiver = transactionObject.getTo();

        EthereumTransaction ethereumTransaction = new EthereumTransaction();

        ethereumTransaction.setValue(transactionObject.getValue().toString());
        ethereumTransaction.setTransactionHash(transactionObject.getHash().startsWith("0x") ? transactionObject.getHash().substring(2) : transactionObject.getHash());

        // Sender address
        ethereumTransaction.setFromAddress(this.ethereumAddressService.getOrCreateByAddress(sender));
        ethereumTransaction.setTime(new Timestamp(ethBlock.getBlock().getTimestamp().longValueExact() * 1000));
        ethereumTransaction.setBlockNumber(ethBlock.getBlock().getNumber().longValueExact());

        // Not interested in this
        /* if (transactionObject.getValue().compareTo(BigInteger.valueOf(0)) <= 0) {
            // Token transaction, creates or contract interaction
            return;
        } */

        if (receiver == null) {
            ethereumTransaction.setToAddress(null);
            ethereumTransaction.setTransactionType(EthereumTransactionType.CONTRACT_CREATION);
            if(transactionObject.getCreates() != null) {
                EthereumAddress ethereumContract = this.ethereumAddressService.createContractByAddress(transactionObject.getCreates().substring(2));
                ethereumTransaction.setEthereumContract(ethereumContract);
            }
        } else {
            receiver = receiver.substring(2, receiver.length()).toLowerCase();
            EthereumAddress receiverAddress = this.ethereumAddressService.getOrCreateByAddress(receiver);
            if (receiverAddress.getIsContract()) {
                ethereumTransaction.setTransactionType(EthereumTransactionType.CONTRACT_TRANSACTION);
                ethereumTransaction.setEthereumContract(receiverAddress);
            } else {
                ethereumTransaction.setTransactionType(EthereumTransactionType.NORMAL);
            }
            ethereumTransaction.setToAddress(receiverAddress);
        }

        transactions.add(ethereumTransaction);

        // Token analysis
        transactionReceipt.getLogs().forEach((log -> {
            EthereumEvent ethereumEvent = new EthereumEvent(log.getAddress(), log.getTopics(), log.getData());
            if(ethereumEvent.isTokenTransfer()) {
                String tokenSender = ethereumEvent.getTopics().wordToAddress(1);
                String tokenReceiver = ethereumEvent.getTopics().wordToAddress(2);
                BigInteger amount = new BigInteger(ethereumEvent.getData(), 16);
                String contractAddress = log.getAddress().substring(2);

                EthereumAddress ethereumContract = this.ethereumAddressService.getOrCreateByAddress(contractAddress);

                if(!ethereumContract.getIsContract()) {
                   ethereumContract.setIsContract(true);
                   ethereumContract = this.ethereumAddressService.save(ethereumContract);
                }

                // Outbound token transactions
                EthereumTransaction tokenTransaction = new EthereumTransaction();
                tokenTransaction.setBlockNumber(ethBlock.getBlock().getNumber().longValueExact());
                tokenTransaction.setEthereumContract(ethereumContract);
                tokenTransaction.setValue(amount.toString());
                tokenTransaction.setTransactionType(EthereumTransactionType.TOKEN_TRANSACTION);
                tokenTransaction.setTime(new Timestamp(ethBlock.getBlock().getTimestamp().longValueExact() * 1000));
                tokenTransaction.setFromAddress(this.ethereumAddressService.getOrCreateByAddress(tokenSender));
                tokenTransaction.setToAddress(this.ethereumAddressService.getOrCreateByAddress(tokenReceiver));
                tokenTransaction.setTransactionHash(transactionObject.getHash().startsWith("0x") ? transactionObject.getHash().substring(2) : transactionObject.getHash());
                transactions.add(tokenTransaction);
            }
        }));

        // Remove the contract transaction for token transactions
        List<EthereumTransaction> contractTransactions =
                transactions.stream().filter(t -> t.getTransactionType() == EthereumTransactionType.CONTRACT_TRANSACTION).collect(Collectors.toList());
        for(int i = 0; i < contractTransactions.size(); i++) {
            EthereumTransaction contractTx = contractTransactions.get(i);
            boolean contains = transactions.stream()
                            .anyMatch(t -> t.getTransactionType() == EthereumTransactionType.TOKEN_TRANSACTION && t.getTransactionHash().equals(contractTx.getTransactionHash()));
            if (contains) transactions.remove(contractTx);
        }

        return transactions;
    }
}
