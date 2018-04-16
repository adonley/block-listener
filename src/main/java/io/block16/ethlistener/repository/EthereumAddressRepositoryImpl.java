package io.block16.ethlistener.repository;

import io.block16.ethlistener.domain.jpa.EthereumAddress;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository

public class EthereumAddressRepositoryImpl implements EthereumAddressRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<EthereumAddress> getContractAddresses(Long id) {
        Query query =
                entityManager.createNativeQuery("SELECT DISTINCT a.* FROM ethereum_transaction t " +
                        "JOIN ethereum_address a ON t.ethereum_contract=a.id " +
                        "WHERE (t.ethereum_contract IS NOT NULL) AND (t.to_address = ? OR t.from_address = ?);", EthereumAddress.class);
        query.setParameter(1, id);
        query.setParameter(2, id);
        return (List<EthereumAddress>)query.getResultList();
    }
}
