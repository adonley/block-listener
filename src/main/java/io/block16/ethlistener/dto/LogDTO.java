package io.block16.ethlistener.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.web3j.protocol.core.methods.response.Log;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class LogDTO {
    private boolean removed;
    private String logIndex;
    private String transactionIndex;
    private String transactionHash;
    private String blockHash;
    private String blockNumber;
    private String address;
    private String data;
    private String type;
    private List<String> topics;

    @JsonIgnore
    public static List<LogDTO> fromWeb3Logs(List<Log> logs) {
        return logs.stream().map(l -> {
            LogDTO dto = new LogDTO();
            dto.setRemoved(l.isRemoved());
            dto.setLogIndex(l.getLogIndexRaw());
            dto.setTransactionIndex(l.getTransactionIndexRaw());
            dto.setTransactionHash(l.getTransactionHash());
            dto.setBlockHash(l.getBlockHash());
            dto.setBlockNumber(l.getBlockNumberRaw());
            dto.setAddress(l.getAddress());
            dto.setData(l.getData());
            dto.setType(l.getType());
            dto.setTopics(l.getTopics());
            return dto;
        }).collect(Collectors.toList());
    }
}
