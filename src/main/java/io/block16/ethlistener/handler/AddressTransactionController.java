package io.block16.ethlistener.handler;

import io.block16.ethlistener.TokenUtilities;
import io.block16.ethlistener.domain.jpa.EthereumAddress;
import io.block16.ethlistener.domain.jpa.EthereumTransaction;
import io.block16.ethlistener.exceptions.BadRequestException;
import io.block16.ethlistener.service.EthereumAddressService;
import io.block16.ethlistener.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class AddressTransactionController {

    private final EthereumAddressService ethereumAddressService;
    private final TransactionService transactionService;

    @Autowired
    public AddressTransactionController(
            final EthereumAddressService ethereumAddressService,
            final TransactionService transactionService
    ) {
        this.ethereumAddressService = ethereumAddressService;
        this.transactionService = transactionService;
    }


    @GetMapping("/v1/address/{address}/transactions")
    public List<EthereumTransaction> transactionsForAddress(@PathVariable String address) {
        if (!TokenUtilities.isAddress(address)) {
            throw new BadRequestException("Address " + address + "is not a valid ethereum address.");
        }
        String a = TokenUtilities.removeHexPrefix(address).toLowerCase();
        Optional<EthereumAddress> ethereumAddress = this.ethereumAddressService.getByAddress(a);
        if (ethereumAddress.isPresent()) {
            return transactionService.getByAddress(ethereumAddress.get());
        }
        return new ArrayList<>();
    }

    @GetMapping("/v1/address/{address}/assets")
    public List<EthereumAddress> assetsForAddress(@PathVariable String address) {
        if (!TokenUtilities.isAddress(address)) {
            throw new BadRequestException("Address " + address + "is not a valid ethereum address.");
        }
        String a = TokenUtilities.removeHexPrefix(address).toLowerCase();
        return this.ethereumAddressService.getContractsAssociatedWithAddress(a);
    }
}
