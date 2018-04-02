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

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_address")
    private EthereumAddress toAddress;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_address")
    private EthereumAddress fromAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private EthereumAddress ethereumContract;

    private String transactionHash;

    private Long blockNumber;

    private Timestamp time;

    @Enumerated(EnumType.STRING)
    private EthereumTransactionType transactionType;


}
