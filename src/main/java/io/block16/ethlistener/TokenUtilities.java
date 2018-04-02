package io.block16.ethlistener;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Hash;

public class TokenUtilities {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenUtilities.class.getSimpleName());
    public static final String TOKEN_TRANSFER_HASH = encodeFunctionDefinition("Transfer(address,address,uint256)");

    public static String encodeFunctionDefinition(String funtionDefinition) {
        return Hex.encodeHexString(Hash.sha3(funtionDefinition.getBytes()));
    }
}
