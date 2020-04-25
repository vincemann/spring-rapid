package com.naturalprogrammer.spring.lemon.auth.mail;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

/**
 * The mail sender interface for sending mail
 */
@ServiceComponent
public interface MailSender<MailData> {

	void send(MailData mail);
}