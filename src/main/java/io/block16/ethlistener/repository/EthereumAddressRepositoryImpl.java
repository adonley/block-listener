package io.block16.ethlistener.repository;

import io.block16.ethlistener.domain.jpa.EthereumAddress;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class EthereumAddressRepositoryImpl implements EthereumAddressRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<EthereumAddress> getContractAddresses(Long id) {
        Query query =
                entityManager.createNativeQuery("SELECT ethereum_address.* FROM ethereum_address " +
                        "JOIN ethereum_transaction ON ethereum_transaction.ethereum_contract=ethereum_address.id WHERE to_address = ? OR " +
                        "from_address = ? AND ethereum_contract IS NOT NULL;", EthereumAddress.class);
        query.setParameter(1, id);
        query.setParameter(2, id);
        return (List<EthereumAddress>)query.getResultList();
    }
}
