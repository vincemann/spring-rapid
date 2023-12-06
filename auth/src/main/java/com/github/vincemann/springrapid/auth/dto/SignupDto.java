package com.github.vincemann.springrapid.auth.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import lombok.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Builder
public class SignupDto implements Serializable {

    //    @UniqueContactInformation(/*groups = {UserVerifyUtils.SignUpValidation.class}*/)
//    @JsonView(UserVerifyUtils.SignupInput.class)
    @NotBlank
//    @ContactInformation
    private String contactInformation;

    //    @Password(/*groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.ChangeContactInformationValidation.class}*/)
//    @JsonView(UserVerifyUtils.SignupInput.class)
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
