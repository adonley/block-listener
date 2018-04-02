package io.block16.ethlistener.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {

    private String nodeLocation;

    @Autowired
    public Web3jConfig(
            @Value("${io.block16.nodeLocation}") String nodeLocation
    ) {
        this.nodeLocation = nodeLocation;
    }

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(nodeLocation));
    }
}
