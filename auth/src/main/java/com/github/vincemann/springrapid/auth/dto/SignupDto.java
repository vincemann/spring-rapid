package com.github.vincemann.springrapid.auth.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class SignupDto implements Serializable {
    //    @UniqueEmail(/*groups = {UserVerifyUtils.SignUpValidation.class}*/)
    @JsonView(UserVerifyUtils.SignupInput.class)
    @NotBlank
    @Email
    private String email;
    //    @Password(/*groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.ChangeEmailValidation.class}*/)
    @JsonView(UserVerifyUtils.SignupInput.class)
    @NotBlank
    private String password;
}
