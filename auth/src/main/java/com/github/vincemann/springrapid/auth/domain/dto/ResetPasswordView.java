package com.github.vincemann.springrapid.auth.domain.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ResetPasswordView {
    private String password;
    private String matchPassword;
}
