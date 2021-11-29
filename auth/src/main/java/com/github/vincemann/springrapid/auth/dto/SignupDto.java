package com.github.vincemann.springrapid.auth.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import com.github.vincemann.springrapid.core.util.LazyInitLogUtils;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
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

    @Override
    public String toString() {
        return LazyInitLogUtils.toString(this);
    }
}
