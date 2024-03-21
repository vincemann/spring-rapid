package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.authdemo.service.ValidUsername;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class SignupDto {

    @NotBlank(message = "{blank.name}")
    @ValidUsername
    private String name;
    private String contactInformation;
    private String password;


    @Builder
    public SignupDto(String contactInformation, String password, String name) {
        this.contactInformation = contactInformation;
        this.password = password;
        this.name = name;
    }
}
