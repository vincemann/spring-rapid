package com.github.vincemann.springrapid.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import com.github.vincemann.springrapid.auth.validation.Password;
import com.github.vincemann.springrapid.auth.validation.UniqueEmail;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class SignupForm {
    @JsonView(UserVerifyUtils.SignupInput.class)
    @UniqueEmail(groups = {UserVerifyUtils.SignUpValidation.class})
    private String email;
    @JsonView(UserVerifyUtils.SignupInput.class)
    @Password(groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.ChangeEmailValidation.class})
    private String password;
}
