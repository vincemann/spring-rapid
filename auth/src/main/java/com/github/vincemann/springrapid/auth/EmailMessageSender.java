package com.github.vincemann.springrapid.auth;

import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.mail.MailSender;
import com.github.vincemann.springrapid.core.util.Message;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailMessageSender implements MessageSender {

    private MailSender<MailData> mailSender;

    @Override
    public void sendMessage(String link, String topic, String code, String contactInformation) {
        // send the mail
        MailData mailData = MailData.builder()
                .to(contactInformation)
//                .topic(Message.get("com.github.vincemann.verifySubject"))
                .topic(topic)
                .body(Message.get("com.github.vincemann.verifyContactInformation", link))
                .link(link)
                .code(code)
                .build();
        mailSender.send(mailData);
    }

    @Autowired
    public void setMailSender(MailSender<MailData> mailSender) {
        this.mailSender = mailSender;
    }




}
