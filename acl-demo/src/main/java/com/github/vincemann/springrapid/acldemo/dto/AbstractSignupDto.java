package com.github.vincemann.springrapid.acldemo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
@Setter
public class AbstractSignupDto {
    @NotEmpty
    @Email
    private String contactInformation;
    @NotEmpty
    private String password;

    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;

    public AbstractSignupDto(String contactInformation, String password, String firstName, String lastName) {
        this.contactInformation = contactInformation;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
