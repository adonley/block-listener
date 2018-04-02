package io.block16.ethlistener.service;

import io.block16.ethlistener.domain.jpa.EthereumTransaction;
import io.block16.ethlistener.listener.DatabaseBuilderListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ListenerService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static String processedBlockKey = "listener::ListenerService::latestBlockProcessed";

    private final RedisTemplate<String, Object> redisTemplate;
    private final SetOperations<String, Object> setOperations;
    private final ValueOperations<String, Object> valueOperations;

    private final ExecutorService executorService;

    private final Web3j web3j;
    private final DatabaseBuilderListener databaseBuilderListener;

    private final TransactionService transactionService;

    private AtomicInteger lastProcessedBlock;
    private BlockingQueue<Integer> workQueue;
    private int addedUpto;

    @Autowired
    public ListenerService(
            final RedisTemplate<String, Object> redisTemplate,
            final Web3j web3j,
            final EthereumAddressService ethereumAddressService,
            final EthereumContractService ethereumContractService,
            final TransactionService transactionService
    ) {
        this.redisTemplate = redisTemplate;
        this.setOperations = this.redisTemplate.opsForSet();
        this.valueOperations = this.redisTemplate.opsForValue();

        this.web3j = web3j;
        this.transactionService = transactionService;

        this.valueOperations.set(processedBlockKey, -1);
        int lastBlockNum = this.valueOperations.get(processedBlockKey) != null ? (Integer) this.valueOperations.get(processedBlockKey) : 0;
        this.lastProcessedBlock = new AtomicInteger(lastBlockNum);
        this.addedUpto = lastBlockNum;
        LOGGER.info("Last processed block: {}", this.lastProcessedBlock.get());

        // Create transaction listener
        this.databaseBuilderListener = new DatabaseBuilderListener(ethereumAddressService, ethereumContractService);

        // Listen for transactions
        this.workQueue = new LinkedBlockingQueue<>();
        this.executorService = Executors.newFixedThreadPool(2);
        this.executorService.submit(this::startWorkerManager);
    }

    /**
     * Check for blocks to add to the queue every 5 seconds.
     * FixedDelay waits a delay until the previous invocation finishes
     */
    @Scheduled(fixedDelay = 3000)
    private void getBlocksToScan() {
        try {
            EthBlockNumber ethBlockNumber = this.web3j.ethBlockNumber().send();

            // Add all new blocks, up to the 2nd to last block
            if (this.addedUpto + 1 <= ethBlockNumber.getBlockNumber().intValue() - 1) {
                for(int i = this.addedUpto + 1; i < ethBlockNumber.getBlockNumber().intValue() - 1; i++) {
                    this.workQueue.put(i);
                    this.addedUpto = i;
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Could not get lastest block number from the node: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private void startWorkerManager() {
        Integer blockNumber = this.lastProcessedBlock.get() + 1;

        boolean shouldTake = true;

        while (true) {
            try {
                if (shouldTake) {
                    blockNumber = this.workQueue.take();
                }

                // Make sure consecutive blocks
                if (blockNumber != this.lastProcessedBlock.get() + 1) {
                    throw new IllegalStateException("Block was not consecutive, lastBlock: " + blockNumber + " currentBlock " + this.lastProcessedBlock.get());
                }

                Future<List<EthereumTransaction>> processedTxs = this.executorService.submit(new BlockWorker(blockNumber));
                List<EthereumTransaction> transactions = processedTxs.get();
                this.transactionService.save(transactions);
                LOGGER.info("Interesting transactions: {}, numberBlocks on queue: {}", transactions, this.workQueue.size());

                // Set the last processed block
                lastProcessedBlock.set(blockNumber);
                valueOperations.set(processedBlockKey, blockNumber);
                shouldTake = true;
            } catch (InterruptedException ie) {
                shouldTake = false;
                LOGGER.error("Thread was interrupted when trying to pull block work");
                ie.printStackTrace();
            } catch (ExecutionException ex) {
                shouldTake = false;
                LOGGER.error("Issue processing block {}, trying again. Message {}", this.lastProcessedBlock.get() + 1, ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private class BlockWorker implements Callable<List<EthereumTransaction>> {
        Integer blockNumber;

        BlockWorker(Integer blockNumber) {
            this.blockNumber = blockNumber;
        }

        @Override
        public List<EthereumTransaction> call() throws Exception {
            List<EthereumTransaction> interestingTransactions = new LinkedList<>();

            BigInteger b = BigInteger.valueOf(blockNumber);

            EthBlock block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(b), true).send();

            List<EthBlock> unclesList = new ArrayList<>(2);
            for(int i = 0; i < block.getBlock().getUncles().size(); i++) {
                unclesList.add(web3j.ethGetUncleByBlockNumberAndIndex(DefaultBlockParameter.valueOf(b), BigInteger.valueOf(i)).send());
            }

            List<EthBlock.TransactionResult> transactions = block.getBlock().getTransactions();

            List<EthGetTransactionReceipt> receipts =
                    transactions.stream()
                            .map(o ->  web3j.ethGetTransactionReceipt(((EthBlock.TransactionObject)o).get().getHash()).sendAsync())
                            .map(CompletableFuture::join)
                            .collect(toList());

            if(receipts.size() != transactions.size()) {
                throw new IllegalStateException("Receipts size was not the same as TX size.");
            }


            databaseBuilderListener.onBlock(block,
                    unclesList,
                    transactions.stream()
                            .map(EthBlock.TransactionResult::get)
                            .map(EthBlock.TransactionObject.class::cast)
                            .collect(toList()),
                    receipts.stream()
                            .map(EthGetTransactionReceipt::getTransactionReceipt)
                            .filter(Optional::isPresent).map(Optional::get)
                            .collect(Collectors.toList()));

            // RPC for all these in this model...
            // This is busted open so we can throw without defining an interface
            for(int i = 0; i < transactions.size(); i++) {
                EthGetTransactionReceipt ethTransactionReceipt = receipts.get(i);

                // Ethereum Transaction information
                EthBlock.TransactionObject transactionObject =  (EthBlock.TransactionObject)transactions.get(i).get();

                if (!ethTransactionReceipt.getTransactionReceipt().isPresent()) {
                    LOGGER.error("Assumption that transaction receipt should be present was false.");
                    throw new IllegalStateException("Assumption that transaction receipt should be present was false.");
                }

                // Token transaction information
                TransactionReceipt transactionReceipt = ethTransactionReceipt.getTransactionReceipt().get();

                databaseBuilderListener.onTransaction(block, transactionObject, transactionReceipt);

            }

            return interestingTransactions;
        }
    }
}
