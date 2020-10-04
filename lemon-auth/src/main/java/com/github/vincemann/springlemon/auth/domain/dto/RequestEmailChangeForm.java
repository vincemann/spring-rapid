package com.github.vincemann.springlemon.auth.domain.dto;

import com.github.vincemann.springlemon.auth.util.UserVerifyUtils;
import com.github.vincemann.springlemon.auth.validation.Password;
import com.github.vincemann.springlemon.auth.validation.UniqueEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class RequestEmailChangeForm {
//    @Password(groups = {UserVerifyUtils.ChangeEmailValidation.class})
//    private String password;
    @UniqueEmail(groups = {UserVerifyUtils.ChangeEmailValidation.class})
    private String newEmail;
}
