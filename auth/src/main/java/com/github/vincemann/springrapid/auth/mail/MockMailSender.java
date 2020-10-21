package com.github.vincemann.springrapid.auth.mail;

import lombok.extern.slf4j.Slf4j;

/**
 * A mock mail sender for 
 * writing the mails to the log.
 * 
 * @author Sanjay Patel
 */
@Slf4j
public class MockMailSender implements MailSender<MailData> {
	

	public MockMailSender() {

	}

	@Override
	public void send(MailData mail) {
		
		log.info("Sending mail to " + mail.getTo());
		log.info("Subject: " + mail.getSubject());
		log.info("Body: " + mail.getBody());
	}

}
