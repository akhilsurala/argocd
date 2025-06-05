package com.sunseed.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailRequestDto {

	private String to;
	private String subject;
	private String body;
	private String from;
	private String cc;

}
