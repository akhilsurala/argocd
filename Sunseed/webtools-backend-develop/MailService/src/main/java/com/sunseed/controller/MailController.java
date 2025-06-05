package com.sunseed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sunseed.service.MailService;

@RestController
@RequestMapping("/mail")
public class MailController {
	@Autowired
private MailService mailService;

	@PostMapping("/send")
	public String sendMail(@RequestParam(value="file",required = false) MultipartFile[] file, @RequestParam("to") String to, @RequestParam(value="cc",required=false) String[] cc, @RequestParam("subject") String subject, @RequestParam("body") String body)
	{				
		System.out.println(to);
		return mailService.sendMail(file,  to, cc, subject, body);
	}
	
}
