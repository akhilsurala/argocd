package com.sunseed.simtool.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageProducer {

	@Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchangeName, String routingKey, String message) {
    	log.info("Sending message {} to exchange {} with routing key {}", message, exchangeName, routingKey);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
        log.info("Successfully send message {} to exchange {} with routing key {}", message, exchangeName, routingKey);
        
    }
}
