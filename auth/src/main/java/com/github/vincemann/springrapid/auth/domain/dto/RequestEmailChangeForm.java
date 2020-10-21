package com.github.vincemann.springrapid.auth.domain.dto;

import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import com.github.vincemann.springrapid.auth.validation.UniqueEmail;
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
