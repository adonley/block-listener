package io.block16.ethlistener.dto;

import lombok.Data;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;

@Data
public class FullBlockDto {
    EthBlock.Block block;
    List<EthBlock.Block> unclesList;
    List<Transaction> transactions;
    List<TransactionReceipt> receipts;
}
