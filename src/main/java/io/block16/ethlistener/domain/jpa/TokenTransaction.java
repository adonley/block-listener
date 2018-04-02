package io.block16.ethlistener.domain.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name="token_transaction")
public class TokenTransaction {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String amount;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private EthereumContract ethereumContract;

    @ManyToOne
    @JoinColumn(name = "to_address")
    private EthereumAddress toAddress;

    @ManyToOne
    @JoinColumn(name = "from_address")
    private EthereumAddress fromAddress;

    private String transactionHash;

    private Timestamp time;
}
