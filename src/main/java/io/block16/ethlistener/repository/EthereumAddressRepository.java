package io.block16.ethlistener.repository;

import io.block16.ethlistener.domain.jpa.EthereumAddress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EthereumAddressRepository extends JpaRepository<EthereumAddress, Long> {
    Optional<EthereumAddress> getByAddress(String address);
    Optional<EthereumAddress> getFirstByAddress(String address);

}
