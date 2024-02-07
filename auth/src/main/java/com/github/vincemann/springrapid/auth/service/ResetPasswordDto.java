package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.service.val.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@AllArgsConstructor
@Data
public class ResetPasswordDto implements Serializable {
    @ValidPassword
    String newPassword;

    @NotBlank
    String code;
}
