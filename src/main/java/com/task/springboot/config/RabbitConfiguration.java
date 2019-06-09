package com.task.springboot.config;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@EnableRabbit
@Configuration
public class RabbitConfiguration {
    public static final String QUEUE_GET_ALL_USERS = "GetAllUsesRequest";
    public static final String QUEUE_USER_FIND_BY_ID = "QueueUserFindByID";
    public static final String QUEUE_USER_FIND_BY_NAME = "QueueUserFindByName";
    public static final String QUEUE_USER_SAVE = "QueueUserSave";
    public static final String QUEUE_USER_UPDATE = "QueueUserUpdate";
    public static final String QUEUE_USER_DELETE_BY_ID = "QueueUserDeleteByID";

    @Autowired
    private Environment env;
    Logger logger = Logger.getLogger(RabbitConfiguration.class);

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory();
                connectionFactory.setHost(env.getProperty("spring.rabbitmq.host"));
                connectionFactory.setUsername(env.getProperty("spring.rabbitmq.username"));
                connectionFactory.setPassword(env.getProperty("spring.rabbitmq.password"));
                connectionFactory.setPort(Integer.parseInt(env.getProperty("spring.rabbitmq.port")));
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        return rabbitAdmin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setReplyTimeout(60 * 1000);
        //no reply to - we use direct-reply-to
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue queueGetAllUsers() {
        return new Queue(QUEUE_GET_ALL_USERS);
    }

    @Bean
    public Queue queueUserFindById() {
        return new Queue(QUEUE_USER_FIND_BY_ID);
    }

    @Bean
    public Queue queueUserFindByName() {
        return new Queue(QUEUE_USER_FIND_BY_NAME);
    }

    @Bean
    public Queue queueUserSave() {
        return new Queue(QUEUE_USER_SAVE);
    }

    @Bean
    public Queue queueUserUpdate() {
        return new Queue(QUEUE_USER_UPDATE);
    }

    @Bean
    public Queue queueUserDeleteById() {
        return new Queue(QUEUE_USER_DELETE_BY_ID);
    }

}