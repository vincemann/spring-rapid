package com.github.vincemann.springrapid.acldemo.dto.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.domain.dto.SignupDto;
import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class MySignupDto extends SignupDto {

    private Boolean vet;


    public MySignupDto(@NotBlank @Email String email, @NotBlank String password, Boolean vet) {
        super(email, password);
        this.vet = vet;
    }
}
