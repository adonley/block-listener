package io.block16.ethlistener.domain;

import com.google.common.base.Splitter;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

import static io.block16.ethlistener.TokenUtilities.TOKEN_TRANSFER_HASH;

@Data
public class EthereumEvent {
    private static final Splitter logDataSplitter = Splitter.fixedLength(64);

    private final String address;
    private final EthereumWords topics;
    private final String data; // Data is arbitrary length

    public EthereumEvent(String address, List<String> topics, String data) {
        // Set address
        String tempAddress = address.toLowerCase();
        if(tempAddress.startsWith("0x")) tempAddress = tempAddress.substring(2);
        this.address = tempAddress;

        // Set topics
        this.topics = new EthereumWords(topics.stream().map(t -> t.substring(2).toLowerCase()).collect(Collectors.toList()));

        // Set data
        if(data != null) {
            String transformedData = data.toLowerCase();
            if(transformedData.startsWith("0x")) transformedData = transformedData.substring(2);
            this.data = transformedData;
        } else {
            this.data = "";
        }
    }

    public boolean isTokenTransfer() {
        return topics.size() == 3 &&
                topics.getWords().get(0).equals(TOKEN_TRANSFER_HASH) &&
                this.data.length() % 64 == 0;
    }
}
