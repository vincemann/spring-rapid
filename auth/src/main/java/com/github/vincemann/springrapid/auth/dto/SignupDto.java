package com.github.vincemann.springrapid.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class SignupDto implements Serializable {

    private String contactInformation;
    private String password;


    @Builder
    public SignupDto(String contactInformation, String password) {
        this.contactInformation = contactInformation;
        this.password = password;
    }

    @Override
    public String toString() {
        return "SignupDto{" +
                "contactInformation='" + contactInformation + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
