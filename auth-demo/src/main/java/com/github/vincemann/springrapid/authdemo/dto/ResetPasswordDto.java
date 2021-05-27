package com.github.vincemann.springrapid.authdemo.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ResetPasswordDto {
    private String password;
    private String matchPassword;
}
