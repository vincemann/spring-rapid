package com.naturalprogrammer.spring.lemon.authdemo.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.LemonSignupForm;
import com.naturalprogrammer.spring.lemon.auth.util.UserUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class MySignupForm extends LemonSignupForm {

    @JsonView(UserUtils.SignupInput.class)
    @NotBlank(message = "{blank.name}", groups = {UserUtils.SignUpValidation.class, UserUtils.UpdateValidation.class})
    private String name;

    public MySignupForm(String email, String password, @NotBlank(message = "{blank.name}", groups = {UserUtils.SignUpValidation.class, UserUtils.UpdateValidation.class}) String name) {
        super(email, password);
        this.name = name;
    }
}
