package io.block16.ethlistener.domain.jpa;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.block16.ethlistener.domain.EthereumTransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name="ethereum_transaction")
public class EthereumTransaction {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String value;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_address")
    private EthereumAddress toAddress;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_address")
    private EthereumAddress fromAddress;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "ethereum_contract")
    private EthereumAddress ethereumContract;

    private String transactionHash;

    private Long blockNumber;

    private Timestamp time;

    @Enumerated(EnumType.STRING)
    private EthereumTransactionType transactionType;


}
