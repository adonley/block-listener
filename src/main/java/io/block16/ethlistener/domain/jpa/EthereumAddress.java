package io.block16.ethlistener.domain.jpa;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    private String name;

    // token
    private String symbol;
    private String iconUrl;
    private Integer decimalPlaces;
    private Boolean isContract = false;

    @PrePersist
    public void prePersist() {
        if(!this.address.isEmpty()) {
            this.address = this.address.toLowerCase();
        }
    }

    @Override
    public String toString() {
        return "EthereumAddress{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", decimalPlaces=" + decimalPlaces +
                ", isContract=" + isContract +
                '}';
    }
}
