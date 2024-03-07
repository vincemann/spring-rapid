package com.github.vincemann.springrapid.auth.msg.mail;

import com.github.vincemann.springrapid.core.util.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * An SMTP mail sender, which sends mails
 * using an injected JavaMailSender.
 * 
 * @author Sanjay Patel
 *
 */
public class SmtpMailSender implements MailSender<MailData> {

	private final Log log = LogFactory.getLog(SmtpMailSender.class);
	

	private final JavaMailSender javaMailSender;
	
	public SmtpMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}


	/**
	 * Sends a mail using a MimeMessageHelper
	 */
	@Override
	@Async
	public void send(MailData mail) {
		
		log.info("Sending SMTP mail from thread " + Thread.currentThread().getName()); // toString gives more info
		log.info(LogMessage.format("mail data: %s",mail.toString()));

		// create a mime-message
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper;

		try {
			
			// create a helper
			helper = new MimeMessageHelper(message, true);
			// set the attributes
			helper.setSubject(mail.getTopic());
			helper.setTo(mail.getTo());
			message.setFrom(Message.get("com.github.vincemann.contactInformationSender"));
			helper.setText(mail.getBody(), true); // true indicates html
			// continue using helper object for more functionalities like adding attachments, etc.  
			
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
		//send the mail
		javaMailSender.send(message);		
		log.info("Sent SMTP mail from thread " + Thread.currentThread().getName());    	
	}

}
