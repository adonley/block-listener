package io.block16.ethlistener.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MapperConfig {

    @Primary
    @Bean
    public ObjectMapper HibernateAwareObjectMapper() {
        Hibernate5Module module = new Hibernate5Module();

        ObjectMapper objectMapper = new ObjectMapper();

        module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);

        module.configure(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION, true);

        objectMapper.registerModule(module);

        return objectMapper;
    }

}

