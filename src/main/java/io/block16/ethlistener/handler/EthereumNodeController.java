package io.block16.ethlistener.handler;

import io.block16.ethlistener.domain.jpa.EthereumAddress;
import io.block16.ethlistener.exceptions.BadRequestException;
import io.block16.ethlistener.exceptions.InternalServerException;
import io.block16.ethlistener.service.EthereumAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;

import java.io.IOException;
import java.util.List;

@RestController
public class EthereumNodeController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final Web3j web3j;
    private final EthereumAddressService ethereumAddressService;

    @Autowired
    public EthereumNodeController(
            final EthereumAddressService ethereumAddressService,
            final Web3j web3j
    ) {
        this.ethereumAddressService = ethereumAddressService;
        this.web3j = web3j;
    }

    @GetMapping(value = "/v1/transaction/{address}/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getTransactionCount(@PathVariable String address) {
        if(address == null || address.isEmpty() || address.length() < 40 || address.length() > 42) {
            LOGGER.error("Address was not correct when getting transaction count: {}", address);
            throw new BadRequestException("Address was not correct when getting transaction count");
        }
        String shortAddress = address.substring(address.length() - 40, address.length());
        try {
            EthGetTransactionCount ethGetTransactionCount =
                    this.web3j.ethGetTransactionCount(shortAddress, DefaultBlockParameter.valueOf("latest")).send();
            return ethGetTransactionCount.getTransactionCount().longValue();
        } catch (IOException ex) {
            throw new InternalServerException("Couldn't contact node for transaction count");
        }
    }

    @PostMapping(value = "/v1/transaction", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean sendRawTransaction() {
        // TODO: Get transaction, send it and watch it. Might require send watching which is different than address watching

        return true;
    }
}
