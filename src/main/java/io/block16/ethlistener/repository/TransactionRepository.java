package io.block16.ethlistener.repository;

import io.block16.ethlistener.domain.jpa.EthereumTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<EthereumTransaction, Long> {
}
