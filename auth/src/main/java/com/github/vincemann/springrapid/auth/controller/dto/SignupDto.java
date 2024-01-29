package com.github.vincemann.springrapid.auth.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Builder
public class SignupDto implements Serializable {

    @NotBlank
    private String contactInformation;

    @NotBlank
    private String password;

    private Set<String> roles = new HashSet<>();

    public SignupDto(String contactInformation, String password, Set<String> roles) {
        this.contactInformation = contactInformation;
        this.password = password;
        if (roles != null)
            this.roles = roles;
    }

    @Override
    public String toString() {
        return "SignupDto{" +
                "contactInformation='" + contactInformation + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}';
    }
}
