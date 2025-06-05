package com.sunseed.simtool.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	
	@Value("${rabbitmq.exchange}")
    private String exchangeName;
	
	@Value("${rabbitmq.queue.pv}")
    private String queueNamePV;
	@Value("${rabbitmq.routingkey.pv}")
	private String routingKeyPV;
	
	@Value("${rabbitmq.queue.agri}")
    private String queueNameAgri;
	@Value("${rabbitmq.routingkey.agri}")
	private String routingKeyAgri;
	
	@Value("${rabbitmq.queue.result}")
    private String resultQueue;
	@Value("${rabbitmq.routingkey.result}")
	private String resultKey;

	@Bean
    DirectExchange exchange() {
        return new DirectExchange(exchangeName, true, false);
    }
    
    @Bean
    Queue pvQueue() {
    	Map<String, Object> args = new HashMap<>();
        args.put("x-queue-type", "quorum");
        return new Queue(queueNamePV, true, false, false, args);
    }
    
    @Bean
    Queue agriQueue() {
    	Map<String, Object> args = new HashMap<>();
        args.put("x-queue-type", "quorum");
        return new Queue(queueNameAgri, true, false, false, args);
    }
    
    @Bean
    Queue resultQueue() {
    	Map<String, Object> args = new HashMap<>();
        args.put("x-queue-type", "quorum");
        return new Queue(resultQueue, true, false, false, args);
    }

    @Bean
    Binding pvBinding(Queue pvQueue, DirectExchange exchange) {
        return BindingBuilder.bind(pvQueue).to(exchange).with(routingKeyPV);
    }
    
    @Bean
    Binding agriBinding(Queue agriQueue, DirectExchange exchange) {
        return BindingBuilder.bind(agriQueue).to(exchange).with(routingKeyAgri);
    }
    
    @Bean
    Binding resultBinding(Queue resultQueue, DirectExchange exchange) {
        return BindingBuilder.bind(resultQueue).to(exchange).with(resultKey);
    }
}
