package io.github.vincemann.springlemon.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.vincemann.springlemon.auth.util.UserUtils;
import io.github.vincemann.springlemon.auth.validation.Password;
import io.github.vincemann.springlemon.auth.validation.UniqueEmail;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class LemonSignupForm {
    @JsonView(UserUtils.SignupInput.class)
    @UniqueEmail(groups = {UserUtils.SignUpValidation.class})
    private String email;
    @JsonView(UserUtils.SignupInput.class)
    @Password(groups = {UserUtils.SignUpValidation.class, UserUtils.ChangeEmailValidation.class})
    private String password;
}
