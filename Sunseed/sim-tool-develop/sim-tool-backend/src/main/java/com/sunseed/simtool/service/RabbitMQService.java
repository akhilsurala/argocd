package com.sunseed.simtool.service;

public interface RabbitMQService {

	public boolean isQueueEmpty(String queueName);
	public int getQueueSize(String queueName);
}
