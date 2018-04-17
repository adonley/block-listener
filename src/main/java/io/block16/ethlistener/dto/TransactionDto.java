package io.block16.ethlistener.dto;

import lombok.Data;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;

import java.util.List;

@Data
public class TransactionDto {
    EthBlock block;
    List<EthBlock> unclesList;
    List<EthBlock.TransactionObject> transactions;
    List<EthGetTransactionReceipt> receipts;
}
