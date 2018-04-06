package io.block16.ethlistener.service;

import io.block16.ethlistener.domain.jpa.EthereumAddress;
import io.block16.ethlistener.domain.jpa.EthereumTransaction;
import io.block16.ethlistener.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(
            final TransactionRepository transactionRepository
    ) {
        this.transactionRepository = transactionRepository;
    }

    public EthereumTransaction save(EthereumTransaction ethereumTransaction) {
        return this.transactionRepository.save(ethereumTransaction);
    }

    // @CacheEvict(value = "addressTransactions", allEntries = true, key = "#ethereumTransactions.address")
    public List<EthereumTransaction> save(List<EthereumTransaction> ethereumTransactions) {
        return this.transactionRepository.save(ethereumTransactions);
    }

    // @Cacheable(value = "addressTransactions", key = "#ethereumAddress.address")
    public List<EthereumTransaction> getByAddress(EthereumAddress ethereumAddress) {
        return this.transactionRepository.getLastTenByAddressId(ethereumAddress.getId());
    }

    public Long getLatestBlock() {
        return this.transactionRepository.getLargestBlock();
    }
}
