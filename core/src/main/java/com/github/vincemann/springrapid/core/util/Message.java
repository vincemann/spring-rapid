package com.github.vincemann.springrapid.core.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class Message {

    private static MessageSource messageSource;

    public Message(MessageSource messageSource) {
        Message.messageSource=messageSource;
    }

    /**
     * Gets a message from messages.properties
     */
    public static String get(String messageKey, Object... args) {

        if (messageSource == null)
            return "ApplicationContext unavailable, probably unit test going on";

        // http://stackoverflow.com/questions/10792551/how-to-obtain-a-current-user-locale-from-spring-without-passing-it-as-a-paramete
        return messageSource.getMessage(messageKey, args,
                LocaleContextHolder.getLocale());
    }
}
