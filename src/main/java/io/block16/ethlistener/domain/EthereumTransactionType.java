package io.block16.ethlistener.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum EthereumTransactionType {
    UNCLE_REWARD("uncle_reward"),
    MINING_REWARD("mining_reward"),

    TOKEN_TRANSACTION("token_transaction"),

    CONTRACT_TRANSACTION("contract_transaction"),

    NORMAL("normal"),

    CONTRACT_CREATION("contract_creation");

    private final String value;

    private static Map<String, EthereumTransactionType> valueMap = new HashMap<>(6);

    static {
        valueMap.put(UNCLE_REWARD.getValue(), UNCLE_REWARD);
        valueMap.put(MINING_REWARD.getValue(), MINING_REWARD);

        valueMap.put(TOKEN_TRANSACTION.getValue(), TOKEN_TRANSACTION);
        valueMap.put(CONTRACT_TRANSACTION.getValue(), CONTRACT_TRANSACTION);

        valueMap.put(NORMAL.getValue(), NORMAL);

        valueMap.put(CONTRACT_CREATION.getValue(), CONTRACT_CREATION);
    }

    EthereumTransactionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public EthereumTransactionType forValue(String value) {
        if(valueMap.containsKey(value.toLowerCase())) throw new IllegalArgumentException("Could not map value " + value + "to InterestingTransactionType");
        return valueMap.get(value.toLowerCase());
    }
}
