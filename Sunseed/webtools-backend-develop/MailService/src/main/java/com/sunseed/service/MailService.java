package com.sunseed.service;

import org.springframework.web.multipart.MultipartFile;

public interface MailService {

	String sendMail(MultipartFile[] file, String to, String[] cc, String subject, String body);
	
}
