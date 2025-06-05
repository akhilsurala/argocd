package com.sunseed.simtool.rabbitmq;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
public class MessageProducerTests {

	@Mock
	private RabbitTemplate rabbitTemplate;
	
	@InjectMocks
	private MessageProducer messageProducer;
	
	@Test
	public void test_sendMessage()
	{
		messageProducer.sendMessage("x.simtool", "simtool", "message");
		
		verify(rabbitTemplate, times(1)).convertAndSend("x.simtool", "simtool", "message");
	}
}
