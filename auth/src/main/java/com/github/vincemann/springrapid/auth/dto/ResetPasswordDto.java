package com.github.vincemann.springrapid.auth.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
public class ResetPasswordDto implements Serializable {
    String newPassword;
    String code;
}
