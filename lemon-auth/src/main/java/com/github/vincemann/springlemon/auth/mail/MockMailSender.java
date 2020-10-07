package com.github.vincemann.springlemon.auth.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A mock mail sender for 
 * writing the mails to the log.
 * 
 * @author Sanjay Patel
 */
@Slf4j
public class MockMailSender implements MailSender<LemonMailData> {
	

	public MockMailSender() {

	}

	@Override
	public void send(LemonMailData mail) {
		
		log.info("Sending mail to " + mail.getTo());
		log.info("Subject: " + mail.getSubject());
		log.info("Body: " + mail.getBody());
	}

}
