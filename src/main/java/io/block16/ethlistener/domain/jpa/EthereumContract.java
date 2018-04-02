package io.block16.ethlistener.domain.jpa;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="ethereum_contract")
public class EthereumContract {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    public Long id;
    public String name;
    public String symbol;
    public String address;
    public String iconUrl;
    public int decimalPlaces;
}
