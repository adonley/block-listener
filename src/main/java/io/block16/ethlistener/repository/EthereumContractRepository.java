package io.block16.ethlistener.repository;

import io.block16.ethlistener.domain.jpa.EthereumContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EthereumContractRepository extends JpaRepository<EthereumContract, Long> {
    Optional<EthereumContract> getEthereumContractByAddress(String address);
}
