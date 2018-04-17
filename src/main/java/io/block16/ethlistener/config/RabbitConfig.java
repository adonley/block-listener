package io.block16.ethlistener.config;

import io.block16.ethlistener.listener.BlockWorkListener;
import io.block16.ethlistener.listener.BlockWorkMessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.web3j.protocol.Web3j;

@Configuration
@Import(Web3jConfig.class)
public class RabbitConfig {
    public static String BLOCK_WORK_EXCHANGE = "listener.block.exchange";
    public static String BLOCK_WORK_QUEUE_NAME = "listener.block.queue";
    public static String BLOCK_ROUTING_KEY = "";

    public static String PERSIST_BLOCK_EXCHANGE = "listener.persist.exchange";
    public static String PERSIST_BLOCK_QUEUE = "istener.persist.queue";
    public static String PERSIST_ROUTING_KEY = "";

    @Value("${amqp.port:5672}")
    private int port = 5672;

    @Value("${amqp.username:guest}")
    private String username = "guest";

    @Value("${amqp.password:guest}")
    private String password = "guest";

    @Value("${amqp.vhost:/}")
    private String virtualHost = "/";

    @Value("${amqp.host:localhost}")
    private String host = "localhost";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        // connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPort(port);
        return connectionFactory;
    }

    @Bean
    public DirectExchange blockWorkExchange() {
        return new DirectExchange(PERSIST_BLOCK_EXCHANGE, true, false);
    }

    /**
     * @return the admin bean that can declare queues etc.
     */
    @Bean
    public AmqpAdmin amqpAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public Queue queue() {
        return new Queue(PERSIST_BLOCK_QUEUE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(blockWorkExchange()).with(PERSIST_ROUTING_KEY);
    }

    @Bean
    public SimpleMessageListenerContainer listenerContainer(RabbitTemplate rabbitTemplate, Web3j web3j) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(BLOCK_WORK_QUEUE_NAME);
        container.setMaxConcurrentConsumers(10);
        MessageListenerAdapter listenerAdapter =
                new MessageListenerAdapter(new BlockWorkListener(web3j, rabbitTemplate), new BlockWorkMessageConverter());
        listenerAdapter.setDefaultListenerMethod("onWork");
        container.setMessageListener(listenerAdapter);
        return container;
    }
}
