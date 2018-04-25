package io.block16.ethlistener.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.web3j.protocol.core.methods.response.EthBlock;

@Data
@NoArgsConstructor
public class FetchedBlockDTO {
    private String number;
    private String hash;
    private String parentHash;
    private String nonce;
    private String sha3Uncles;
    private String logsBloom;
    private String transactionsRoot;
    private String stateRoot;
    private String receiptsRoot;
    private String author;
    private String miner;
    private String mixHash;
    private String difficulty;
    private String totalDifficulty;
    private String extraData;
    private String size;
    private String gasLimit;
    private String gasUsed;
    private String timestamp;

    @JsonIgnore
    public static FetchedBlockDTO fromWeb3Block(EthBlock.Block block) {
        FetchedBlockDTO dto = new FetchedBlockDTO();
        dto.setNumber(block.getNumberRaw());
        dto.setHash(block.getHash());
        dto.setParentHash(block.getParentHash());
        dto.setNonce(block.getNonceRaw());
        dto.setSha3Uncles(block.getSha3Uncles());
        dto.setLogsBloom(block.getLogsBloom());
        dto.setTransactionsRoot(block.getTransactionsRoot());
        dto.setStateRoot(block.getStateRoot());
        dto.setReceiptsRoot(block.getReceiptsRoot());
        dto.setAuthor(block.getAuthor());
        dto.setMiner(block.getMiner());
        dto.setMixHash(block.getMixHash());
        dto.setDifficulty(block.getDifficultyRaw());
        dto.setTotalDifficulty(block.getTotalDifficultyRaw());
        dto.setExtraData(block.getExtraData());
        dto.setSize(block.getSizeRaw());
        dto.setGasLimit(block.getGasLimitRaw());
        dto.setGasUsed(block.getGasUsedRaw());
        dto.setTimestamp(block.getTimestampRaw());
        // uncles, sealfields + transactions
        return dto;
    }
}
