package com.github.vincemann.springrapid.auth.msg;

import com.github.vincemann.springrapid.auth.msg.mail.MailData;
import com.github.vincemann.springrapid.auth.msg.mail.MailSender;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailMessageSender implements MessageSender {

    private MailSender<MailData> mailSender;

    @Override
    public void send(AuthMessage message) {
        MailData mailData = MailData.Builder.builder()
                .to(message.getRecipient())
                .topic(message.getTopic())
                .body(message.getBody())
                .link(message.getLink())
                .code(message.getCode())
                .build();
        mailSender.send(mailData);
    }

    @Autowired
    public void setMailSender(MailSender<MailData> mailSender) {
        this.mailSender = mailSender;
    }




}
