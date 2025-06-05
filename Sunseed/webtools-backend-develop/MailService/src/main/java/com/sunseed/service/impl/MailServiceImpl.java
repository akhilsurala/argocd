package com.sunseed.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sunseed.service.MailService;

import jakarta.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {
	
	@Value("${spring.mail.username}")
	private String from; 
	 	
	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public String sendMail(MultipartFile[] file, String to, String[] cc, String subject, String body) {
		
		 try {
	            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

	            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

	            mimeMessageHelper.setFrom(from);
	            mimeMessageHelper.setTo(to);
	            if(cc != null && cc.length > 0) {
	            mimeMessageHelper.setCc(cc);
	            }
	            mimeMessageHelper.setSubject(subject);
	          //  mimeMessageHelper.setText(body);
	            mimeMessageHelper.setText(body.toString(), true);
	            if(file != null && file.length > 0) {
	            for (int i = 0; i < file.length; i++) {
	                mimeMessageHelper.addAttachment(
	                        file[i].getOriginalFilename(),
	                        new ByteArrayResource(file[i].getBytes()));
	            }
	            }

	            javaMailSender.send(mimeMessage);

	            return "mail send";
	            
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	}
	


	
	

}
