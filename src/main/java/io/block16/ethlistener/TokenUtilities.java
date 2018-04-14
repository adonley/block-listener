package io.block16.ethlistener;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Hash;

import java.util.regex.Pattern;

public class TokenUtilities {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenUtilities.class.getSimpleName());
    public static final String TOKEN_TRANSFER_HASH = encodeFunctionDefinition("Transfer(address,address,uint256)");
    public static final String addressPattern = "^(0x){0,1}[0-9a-fA-F]{40}$";

    public static String encodeFunctionDefinition(String funtionDefinition) {
        return Hex.encodeHexString(Hash.sha3(funtionDefinition.getBytes()));
    }

    public static String removeHexPrefix(String s) {
        return s.startsWith("0x") ? s.substring(2) : s;
    }

    public static boolean isAddress(String a) {
        return a.matches(addressPattern);
    }
}
