package io.github.spring.lemon.auth.domain.dto;

import io.github.spring.lemon.auth.util.UserUtils;
import io.github.spring.lemon.auth.validation.Password;
import io.github.spring.lemon.auth.validation.UniqueEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class RequestEmailChangeForm {
    @Password(groups = {UserUtils.ChangeEmailValidation.class})
    private String password;
    @UniqueEmail(groups = {UserUtils.ChangeEmailValidation.class})
    private String newEmail;
}
