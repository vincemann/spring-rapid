package com.github.vincemann.springrapid.auth.mail;

import lombok.Getter;
import lombok.Setter;

/**
 * Data needed for sending a mail.
 * Override this if you need more data to be sent.
 */
@Getter @Setter
public class MailData {
	
	private String to;
	private String subject;
	private String body;

	public static MailData of(String to, String subject, String body) {
		
		MailData data = new MailData();
		
		data.to = to;
		data.subject = subject;
		data.body = body;

		return data;
	}
}
