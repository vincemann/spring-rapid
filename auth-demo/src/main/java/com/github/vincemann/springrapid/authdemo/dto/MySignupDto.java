package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.auth.dto.SignupDto;

import com.github.vincemann.springrapid.authdemo.service.ValidUsername;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class MySignupDto extends SignupDto {

    @NotBlank(message = "{blank.name}")
    @ValidUsername
    private String name;


    @Builder(builderMethodName = "Builder")
    public MySignupDto(String contactInformation, String password, String name) {
        super(contactInformation, password);
        this.name = name;
    }
}
