package com.naturalprogrammer.spring.lemon.auth.mail;

/**
 * The mail sender interface for sending mail
 */
public interface MailSender<MailData> {

	void send(MailData mail);
}