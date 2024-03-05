package com.github.vincemann.springrapid.auth.msg.mail;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


@Getter @Setter
public class MailData {
	
	private String to;
	private String topic;
	private String body;
	private String link;
	private String code;

	@Builder
	public MailData(String to, String topic, String body, String link, String code) {
		this.to = to;
		this.topic = topic;
		this.body = body;
		this.link = link;
		this.code = code;
	}

	@Override
	public String toString() {
		return "MailData{" +
				"to='" + to + '\'' +
				", topic='" + topic + '\'' +
				", body='" + body + '\'' +
				", link='" + link + '\'' +
				", code='" + code + '\'' +
				'}';
	}
}
