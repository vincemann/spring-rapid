package com.naturalprogrammer.spring.lemon.auth.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.naturalprogrammer.spring.lemon.auth.util.UserUtils;
import com.naturalprogrammer.spring.lemon.auth.validation.Password;
import com.naturalprogrammer.spring.lemon.auth.validation.UniqueEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RequestEmailChangeForm {
    @Password(groups = {UserUtils.ChangeEmailValidation.class})
    private String password;
    @UniqueEmail(groups = {UserUtils.ChangeEmailValidation.class})
    private String newEmail;
}
