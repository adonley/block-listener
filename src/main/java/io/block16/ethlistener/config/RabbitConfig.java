package io.block16.ethlistener.config;

import com.google.common.util.concurrent.RateLimiter;
import io.block16.ethlistener.listener.BlockWorkListener;
import io.block16.ethlistener.listener.BlockWorkMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.core.task.TaskExecutor;
import org.web3j.protocol.Web3j;

@Configuration
@Import(Web3jConfig.class)
public class RabbitConfig {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public static String BLOCK_WORK_EXCHANGE = "listener.block.exchange";
    public static String BLOCK_WORK_QUEUE_NAME = "listener.block.queue";
    public static String BLOCK_ROUTING_KEY = "";

    public static String PERSIST_BLOCK_EXCHANGE = "listener.persist.exchange";
    public static String PERSIST_BLOCK_QUEUE = "listener.persist.queue";
    public static String PERSIST_ROUTING_KEY = "";

    public static String BROADCAST_BLOCK_EXCHANGE = "broadcast.exchange";
    public static String BROADCAST_ROUTING_KEY = "";

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

    @Value("${io.block16.concurrency}")
    private int concurrency;

    @Value("${io.block16.nodeLocation}")
    private String nodeLocation;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        // connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPort(port);
        return connectionFactory;
    }

    /* @Bean
    public DirectExchange blockWorkExchange() {
        return new DirectExchange(PERSIST_BLOCK_EXCHANGE, true, false);
    } */

    @Bean
    public FanoutExchange persistBlockExchange() {
        return new FanoutExchange(BROADCAST_BLOCK_EXCHANGE, false, true);
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

    /* @Bean
    public Queue queue() {
        return new Queue(PERSIST_BLOCK_QUEUE);
    } */

    /* @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(blockWorkExchange()).with(PERSIST_ROUTING_KEY);
    } */

    @Bean
    public SimpleMessageListenerContainer listenerContainer(RabbitTemplate rabbitTemplate, Web3j web3j, TaskExecutor taskExecutor) {
        LOGGER.info("Using: " + nodeLocation + " for blockchain related info");
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(BLOCK_WORK_QUEUE_NAME);
        container.setMaxConcurrentConsumers(concurrency);
        container.setPrefetchCount(100);
        container.setTaskExecutor(taskExecutor);
        container.setConsecutiveActiveTrigger(1);
        container.setExclusive(false);
        MessageListenerAdapter listenerAdapter =
                new MessageListenerAdapter(new BlockWorkListener(web3j, rabbitTemplate), new BlockWorkMessageConverter());
        listenerAdapter.setDefaultListenerMethod("onWork");
        container.setMessageListener(listenerAdapter);
        return container;
    }
}
