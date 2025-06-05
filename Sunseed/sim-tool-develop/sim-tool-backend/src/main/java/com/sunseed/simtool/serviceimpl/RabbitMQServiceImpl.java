package com.sunseed.simtool.serviceimpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sunseed.simtool.service.RabbitMQService;

@Service
public class RabbitMQServiceImpl implements RabbitMQService{
	
	@Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.management.url}")
    private String managementUrl;

    public boolean isQueueEmpty(String queueName) {
        int size = getQueueSize(queueName.trim());
        return size == 0;
    }

    public int getQueueSize(String queueName) {
        String url = managementUrl + "/queues/%2F/" + queueName;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map body = response.getBody();
            Object messages = body.get("messages");
            if (messages instanceof Number) {
                return ((Number) messages).intValue();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Failed to fetch queue size for " + queueName + ": " + e.getMessage());
            return -1; // not being able to get the size
        }
    }

}
