package io.block16.ethlistener.service;

import io.block16.ethlistener.domain.jpa.EthereumAddress;
import io.block16.ethlistener.repository.EthereumAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EthereumAddressService {

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
}
