package com.rengu.operationsoanagementsuite.RabbitMQ;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMesssage(String routingKey, String message) {
        ConnectionFactory connectionFactory = new CachingConnectionFactory();
        AmqpAdmin amqpAdmin = new RabbitAdmin(connectionFactory);
        rabbitTemplate.convertAndSend(routingKey, message);
    }
}