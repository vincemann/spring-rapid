package com.github.vincemann.springrapid.auth.msg.mail;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import org.springframework.stereotype.Component;

/**
 * The mail sender interface for sending mail
 */

@LogInteraction
public interface MailSender<MailData> extends AopLoggable {

	void send(MailData mail);
}