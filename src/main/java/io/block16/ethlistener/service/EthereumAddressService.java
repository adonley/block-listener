package io.block16.ethlistener.service;

import io.block16.ethlistener.domain.jpa.EthereumAddress;
import io.block16.ethlistener.exceptions.InternalServerException;
import io.block16.ethlistener.repository.EthereumAddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EthereumAddressService {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final EthereumAddressRepository ethereumAddressRepository;

    @Autowired
    public EthereumAddressService(
            final EthereumAddressRepository ethereumAddressRepository
    ) {
        this.ethereumAddressRepository = ethereumAddressRepository;
    }

    public Optional<EthereumAddress> getByAddress(String address) {
        return this.ethereumAddressRepository.getByAddress(address);
    }

    public EthereumAddress save(EthereumAddress ethereumAddress) {
        return this.ethereumAddressRepository.save(ethereumAddress);
    }

    public EthereumAddress getOrCreateByAddress(String address) {
        Optional<EthereumAddress> ethereumAddressOptional = this.ethereumAddressRepository.getByAddress(address);
        if(ethereumAddressOptional.isPresent()) {
            return ethereumAddressOptional.get();
        } else {
            EthereumAddress ethereumAddress = new EthereumAddress();
            ethereumAddress.setAddress(address);
            return this.ethereumAddressRepository.save(ethereumAddress);
        }
    }

    public EthereumAddress createContractByAddress(String address) {
        Optional<EthereumAddress> ethereumAddressOptional = this.ethereumAddressRepository.getByAddress(address);
        if (ethereumAddressOptional.isPresent()) {
            LOGGER.error("Tried to create a contract with the same address - {}", address);
            throw new InternalServerException("Contract already exists with that address... " + address);
        }
        EthereumAddress ethereumAddress = new EthereumAddress();
        ethereumAddress.setAddress(address);
        ethereumAddress.setIsContract(true);
        return ethereumAddressRepository.save(ethereumAddress);
    }
}
