package com.github.vincemann.springrapid.authdemo.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.domain.dto.SignupForm;
import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class MySignupForm extends SignupForm {

    @JsonView(UserVerifyUtils.SignupInput.class)
    @NotBlank(message = "{blank.name}"/*, groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.UpdateValidation.class}*/)
    private String name;

    public MySignupForm(String email, String password, @NotBlank(message = "{blank.name}", groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.UpdateValidation.class}) String name) {
        super(email, password);
        this.name = name;
    }
}
