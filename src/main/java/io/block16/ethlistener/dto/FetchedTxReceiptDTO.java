package io.block16.ethlistener.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;

@Data
@NoArgsConstructor
public class FetchedTxReceiptDTO {
    private String transactionHash;
    private String transactionIndex;
    private String blockHash;
    private String blockNumber;
    private String cumulativeGasUsed;
    private String gasUsed;
    private String contractAddress;
    private String root;
    // status is only present on Byzantium transactions onwards
    // see EIP 658 https://github.com/ethereum/EIPs/pull/658
    private String status;
    private String from;
    private String to;
    private List<LogDTO> logs;
    private String logsBloom;

    @JsonIgnore
    public static FetchedTxReceiptDTO fromWeb3Receipt(TransactionReceipt transactionReceipt) {
        FetchedTxReceiptDTO dto = new FetchedTxReceiptDTO();
        dto.setTransactionHash(transactionReceipt.getTransactionHash());
        dto.setTransactionIndex(transactionReceipt.getTransactionIndexRaw());
        dto.setBlockHash(transactionReceipt.getBlockHash());
        dto.setBlockNumber(transactionReceipt.getBlockNumberRaw());
        dto.setCumulativeGasUsed(transactionReceipt.getCumulativeGasUsedRaw());
        dto.setGasUsed(transactionReceipt.getGasUsedRaw());
        dto.setContractAddress(transactionReceipt.getContractAddress());
        dto.setRoot(transactionReceipt.getRoot());
        dto.setStatus(transactionReceipt.getStatus());
        dto.setFrom(transactionReceipt.getFrom());
        dto.setTo(transactionReceipt.getTo());
        dto.setLogs(LogDTO.fromWeb3Logs(transactionReceipt.getLogs()));
        dto.setLogsBloom(transactionReceipt.getLogsBloom());
        return dto;
    }
}
