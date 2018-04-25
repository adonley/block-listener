package io.block16.ethlistener.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.web3j.protocol.core.methods.response.EthBlock;

@Data
@NoArgsConstructor
public class FetchedTxDTO {
    private String hash;
    private String nonce;
    private String blockHash;
    private String blockNumber;
    private String transactionIndex;
    private String from;
    private String to;
    private String value;
    private String gasPrice;
    private String gas;
    private String input;
    private String creates;
    private String publicKey;
    private String raw;
    private String r;
    private String s;
    private int v;  // see https://github.com/web3j/web3j/issues/44

    @JsonIgnore
    public static FetchedTxDTO fromWeb3Tx(EthBlock.TransactionObject transactionObject) {
        FetchedTxDTO dto = new FetchedTxDTO();
        dto.setHash(transactionObject.getHash());
        dto.setNonce(transactionObject.getNonceRaw());
        dto.setBlockHash(transactionObject.getBlockHash());
        dto.setBlockNumber(transactionObject.getBlockNumberRaw());
        dto.setTransactionIndex(transactionObject.getTransactionIndexRaw());
        dto.setFrom(transactionObject.getFrom());
        dto.setTo(transactionObject.getTo());
        dto.setValue(transactionObject.getValueRaw());
        dto.setGasPrice(transactionObject.getGasPriceRaw());
        dto.setGas(transactionObject.getGasRaw());
        dto.setInput(transactionObject.getInput());
        dto.setCreates(transactionObject.getCreates());
        dto.setPublicKey(transactionObject.getPublicKey());
        dto.setRaw(transactionObject.getRaw());
        dto.setR(transactionObject.getR());
        dto.setS(transactionObject.getS());
        dto.setV(transactionObject.getV());
        return dto;
    }
}
