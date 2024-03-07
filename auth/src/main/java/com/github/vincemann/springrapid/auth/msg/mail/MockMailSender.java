package com.github.vincemann.springrapid.auth.msg.mail;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A mock mail sender for 
 * writing the mails to the log.
 * 
 * @author Sanjay Patel
 */

public class MockMailSender implements MailSender<MailData> {

	private final Log log = LogFactory.getLog(MockMailSender.class);
	

	public MockMailSender() {

	}

	@Override
	public void send(MailData mail) {
		log.info("Sending mail to " + mail.getTo());
		log.info("Subject: " + mail.getTopic());
		log.info("Body: " + mail.getBody());
	}

}
