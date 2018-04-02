package io.block16.ethlistener.domain.jpa;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name="ethereum_address")
public class EthereumAddress {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    private String address;

    @OneToMany(mappedBy = "toAddress", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<TokenTransaction> inboundTokenTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "fromAddress", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<TokenTransaction> outboundTokenTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "toAddress", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<EthereumTransaction> inboundEthereumTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "fromAddress", cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<EthereumTransaction> outboundEthereumTransactions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if(!this.address.isEmpty()) {
            this.address = this.address.toLowerCase();
        }
    }
}
