package com.github.vincemann.springrapid.auth.mail;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;

/**
 * The mail sender interface for sending mail
 */
@ServiceComponent
@LogInteraction
public interface MailSender<MailData> extends AopLoggable {

	void send(MailData mail);
}