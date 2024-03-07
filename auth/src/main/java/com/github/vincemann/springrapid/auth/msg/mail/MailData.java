package com.github.vincemann.springrapid.auth.msg.mail;


public class MailData {
	
	private String to;
	private String topic;
	private String body;
	private String link;
	private String code;

	public MailData(String to, String topic, String body, String link, String code) {
		this.to = to;
		this.topic = topic;
		this.body = body;
		this.link = link;
		this.code = code;
	}



	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
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

	public static final class Builder {
		private String to;
		private String topic;
		private String body;
		private String link;
		private String code;

		private Builder() {
		}

		public static Builder builder() {
			return new Builder();
		}

		public Builder to(String to) {
			this.to = to;
			return this;
		}

		public Builder topic(String topic) {
			this.topic = topic;
			return this;
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder link(String link) {
			this.link = link;
			return this;
		}

		public Builder code(String code) {
			this.code = code;
			return this;
		}

		public MailData build() {
			return new MailData(to, topic, body, link, code);
		}
	}
}
