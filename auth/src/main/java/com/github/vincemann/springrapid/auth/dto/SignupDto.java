package com.github.vincemann.springrapid.auth.dto;

import com.github.vincemann.springrapid.auth.service.val.ValidContactInformation;
import com.github.vincemann.springrapid.auth.service.val.ValidPassword;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
