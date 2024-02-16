package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.auth.dto.SignupDto;

import com.github.vincemann.springrapid.authdemo.service.val.ValidUsername;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class MySignupDto extends SignupDto {

    @ValidUsername
    @NotBlank(message = "{blank.name}")
    private String name;


    @Builder(builderMethodName = "Builder")
    public MySignupDto(String contactInformation, String password, String name) {
        super(contactInformation, password);
        this.name = name;
    }
}
