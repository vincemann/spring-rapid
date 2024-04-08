package com.github.vincemann.springrapid.acldemo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class AbstractSignupDto {
    @NotEmpty
    @Email
    private String contactInformation;

    @NotEmpty
    @Size(min=8,max= 255)
    private String password;

    @NotEmpty
    @Size(min=2,max=20)
    private String firstName;

    @NotEmpty
    @Size(min=2,max=20)
    private String lastName;

    public AbstractSignupDto(String contactInformation, String password, String firstName, String lastName) {
        this.contactInformation = contactInformation;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
