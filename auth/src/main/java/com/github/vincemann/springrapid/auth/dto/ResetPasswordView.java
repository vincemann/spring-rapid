package com.github.vincemann.springrapid.auth.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ResetPasswordView {
    private String password;
    private String matchPassword;
}
