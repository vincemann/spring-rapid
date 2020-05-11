package com.naturalprogrammer.spring.lemon.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.naturalprogrammer.spring.lemon.auth.util.UserUtils;
import com.naturalprogrammer.spring.lemon.auth.validation.Password;
import com.naturalprogrammer.spring.lemon.auth.validation.UniqueEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class LemonSignupForm {
    @JsonView(UserUtils.SignupInput.class)
    @UniqueEmail(groups = {UserUtils.SignUpValidation.class})
    private String email;
    @JsonView(UserUtils.SignupInput.class)
    @Password(groups = {UserUtils.SignUpValidation.class, UserUtils.ChangeEmailValidation.class})
    private String password;
}
