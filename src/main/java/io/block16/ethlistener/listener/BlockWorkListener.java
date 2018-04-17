package io.block16.ethlistener.listener;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.block16.ethlistener.config.RabbitConfig;
import io.block16.ethlistener.dto.BlockWorkDto;
import io.block16.ethlistener.dto.TransactionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


public class BlockWorkListener {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final RabbitTemplate rabbitTemplate;
    private final Web3j web3j;

    public BlockWorkListener(
            final Web3j web3j,
            final RabbitTemplate rabbitTemplate
    ) {
        this.web3j = web3j;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void onWork(BlockWorkDto blockWorkDto) throws IOException {
        LOGGER.info(blockWorkDto.toString());

        BigInteger b = BigInteger.valueOf(blockWorkDto.getBlockNumber());

        EthBlock block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(b), true).send();

        List<EthBlock> unclesList = new ArrayList<>(2);
        for (int i = 0; i < block.getBlock().getUncles().size(); i++) {
            unclesList.add(web3j.ethGetUncleByBlockNumberAndIndex(DefaultBlockParameter.valueOf(b), BigInteger.valueOf(i)).send());
        }

        List<EthBlock.TransactionObject> transactions =
                block.getBlock().getTransactions().stream().map(o -> (EthBlock.TransactionObject)o.get()).collect(Collectors.toList());

        List<EthGetTransactionReceipt> receipts =
                transactions.stream()
                        .map(o -> web3j.ethGetTransactionReceipt(( o).get().getHash()).sendAsync())
                        .map(CompletableFuture::join)
                        .collect(toList());

        if (receipts.size() != transactions.size()) {
            throw new IllegalStateException("Receipts size was not the same as TX size.");
        }

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setBlock(block);
        transactionDto.setUnclesList(unclesList);
        transactionDto.setTransactions(transactions);
        transactionDto.setReceipts(receipts);

        this.rabbitTemplate.convertAndSend(RabbitConfig.PERSIST_BLOCK_EXCHANGE, RabbitConfig.PERSIST_ROUTING_KEY, objectMapper.writeValueAsString(transactionDto));
    }
}
