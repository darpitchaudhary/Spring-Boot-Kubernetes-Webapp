package com.csye7125.notifier.util;

import com.csye7125.notifier.dao.StoryDao;
import com.csye7125.notifier.dto.EmailRequestDto;
import com.csye7125.notifier.exception.AwsSesClientException;
import com.csye7125.notifier.service.AwsSesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

	private final AwsSesService awsSesService;

	private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

	@Autowired
	public EmailUtil(AwsSesService awsSesService) {
		this.awsSesService = awsSesService;
	}

	public ResponseEntity<String> sendEmail(EmailRequestDto emailRequestDto) {
		try {
			awsSesService.sendEmail(emailRequestDto.getEmail(), emailRequestDto.getBody());
			logger.info("Email successfully sent");
			return ResponseEntity.ok("Successfully Sent Email");
		} catch (AwsSesClientException e) {
			logger.error("Some exception occurred while sending email"+e.getMessage());
			return ResponseEntity.status(500).body("Error occurred while sending email " + e);
		}
	}
}
