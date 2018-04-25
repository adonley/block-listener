package io.block16.ethlistener.dto;

import lombok.Data;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;

@Data
public class FullBlockDto {
    FetchedBlockDTO block;
    List<FetchedBlockDTO> unclesList;
    List<FetchedTxDTO> transactions;
    List<FetchedTxReceiptDTO> receipts;
}
