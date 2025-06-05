//package com.sunseed.simtool.rabbitmq;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class MessageController {
//
//	@Autowired
//    private MessageProducer messageProducer;
//	
//	@Value("${rabbitmq.exchange}")
//    private String exchangeName;
//	@Value("${rabbitmq.routingkey}")
//	private String routingKey;
//
//    @PostMapping("/send")
//    public String sendMessage(@RequestBody String message) {
//        messageProducer.sendMessage(exchangeName, routingKey, message);
//        return "Message sent successfully";
//    }
//}
//
