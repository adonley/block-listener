package io.block16.ethlistener.service;

import io.block16.ethlistener.domain.jpa.EthereumContract;
import io.block16.ethlistener.repository.EthereumContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EthereumContractService {

    private final EthereumContractRepository ethereumContractRepository;

    @Autowired
    public EthereumContractService(
            final EthereumContractRepository ethereumContractRepository
    ) {
        this.ethereumContractRepository = ethereumContractRepository;
    }

    public EthereumContract createOrUpdateContract(String address, String symbol, String name, int decimalPlaces) {
        Optional<EthereumContract> ethereumContractOptional =
                this.ethereumContractRepository.getEthereumContractByAddress(address);
        if(ethereumContractOptional.isPresent()) {
            EthereumContract ethereumContract = ethereumContractOptional.get();
            ethereumContract.setName(name);
            ethereumContract.setSymbol(symbol);
            ethereumContract.setDecimalPlaces(decimalPlaces);
            return this.ethereumContractRepository.save(ethereumContract);
        } else {
            EthereumContract ethereumContract = new EthereumContract();
            ethereumContract.setAddress(address);
            ethereumContract.setName(name);
            ethereumContract.setSymbol(symbol);
            ethereumContract.setDecimalPlaces(decimalPlaces);
            return this.ethereumContractRepository.save(ethereumContract);
        }
    }

    public EthereumContract getOrCreateContract(String address) {
        Optional<EthereumContract> ethereumContractOptional =
                this.ethereumContractRepository.getEthereumContractByAddress(address);
        if(ethereumContractOptional.isPresent()) {
            return ethereumContractOptional.get();
        } else {
            EthereumContract ethereumContract = new EthereumContract();
            ethereumContract.setAddress(address);
            this.ethereumContractRepository.save(ethereumContract);
            return ethereumContract;
        }
    }

    public Optional<EthereumContract> getContractByAddress(String address) {
        return this.ethereumContractRepository.getEthereumContractByAddress(address);
    }

}
