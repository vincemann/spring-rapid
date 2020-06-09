package com.github.vincemann.springlemon.auth.mail;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

/**
 * The mail sender interface for sending mail
 */
@ServiceComponent
public interface MailSender<MailData> {

	void send(MailData mail);
}