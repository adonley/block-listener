package io.block16.ethlistener.repository;

import io.block16.ethlistener.domain.jpa.EthereumAddress;
import io.block16.ethlistener.domain.jpa.EthereumTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<EthereumTransaction, Long> {
    @Query(value = "SELECT * FROM ethereum_transaction WHERE to_address = ?1 OR from_address = ?1 OR ethereum_contract = ?1 ORDER BY time DESC LIMIT 10", nativeQuery = true)
    List<EthereumTransaction> getLastTenByAddressId(Long id);

    @Query(value = "SELECT MAX(block_number) FROM ethereum_transaction", nativeQuery = true)
    Long getLargestBlock();
    // @Query(value = "SELECT ")
    // List<EthereumTransaction> getUniqueAssets(Long id);
}
