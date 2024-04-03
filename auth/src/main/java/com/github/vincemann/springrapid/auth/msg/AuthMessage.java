package com.github.vincemann.springrapid.auth.msg;

public class AuthMessage {
    private String link;
    private String topic;
    private String code;
    private String recipient;
    private String body;

    public AuthMessage(String link, String topic, String code, String recipient, String body) {
        this.link = link;
        this.topic = topic;
        this.code = code;
        this.recipient = recipient;
        this.body = body;
    }

    public AuthMessage() {
    }


    public String getLink() {
        return link;
    }

    public String getTopic() {
        return topic;
    }

    public String getCode() {
        return code;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "AuthMessage{" +
                "link='" + link + '\'' +
                ", topic='" + topic + '\'' +
                ", code='" + code + '\'' +
                ", recipient='" + recipient + '\'' +
                '}';
    }

    public static final class Builder {
        private String link;
        private String topic;
        private String code;
        private String recipient;

        private String body;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder link(String link) {
            this.link = link;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder recipient(String recipient) {
            this.recipient = recipient;
            return this;
        }

        public AuthMessage build() {
            return new AuthMessage(link, topic, code, recipient, body);
        }
    }
}
