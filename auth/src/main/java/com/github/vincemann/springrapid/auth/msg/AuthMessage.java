package com.github.vincemann.springrapid.auth.msg;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class AuthMessage {
    private String link;
    private String topic;
    private String code;
    private String recipient;
}
