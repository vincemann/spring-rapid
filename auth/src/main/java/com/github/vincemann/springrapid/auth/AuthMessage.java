package com.github.vincemann.springrapid.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthMessage {
    private String link;
    private String topic;
    private String code;
    private String recipient;
}
