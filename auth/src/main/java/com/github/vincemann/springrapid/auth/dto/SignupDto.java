package com.github.vincemann.springrapid.auth.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class SignupDto implements Serializable {
    //    @UniqueContactInformation(/*groups = {UserVerifyUtils.SignUpValidation.class}*/)
    @JsonView(UserVerifyUtils.SignupInput.class)
    @NotBlank
//    @ContactInformation
    private String contactInformation;
    //    @Password(/*groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.ChangeContactInformationValidation.class}*/)
    @JsonView(UserVerifyUtils.SignupInput.class)
    @NotBlank
    private String password;

}
