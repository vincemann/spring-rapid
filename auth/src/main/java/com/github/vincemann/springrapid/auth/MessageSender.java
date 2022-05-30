package com.github.vincemann.springrapid.auth;

public interface MessageSender {

    public void sendMessage(String link, String topic, String code, String target);

}
