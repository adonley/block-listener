package io.block16.ethlistener.repository;

import io.block16.ethlistener.domain.jpa.EthereumAddress;

import java.util.List;

public interface EthereumAddressRepositoryCustom {
    List<EthereumAddress> getContractAddresses(Long id);
}
