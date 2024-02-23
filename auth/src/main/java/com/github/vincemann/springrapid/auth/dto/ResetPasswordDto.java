package com.github.vincemann.springrapid.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetPasswordDto implements Serializable {
    String newPassword;
    String code;
}
