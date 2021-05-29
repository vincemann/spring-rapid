package com.github.vincemann.springrapid.auth.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class RequestEmailChangeDto {
    // no pw bc token is used for authentication
//    @Password(groups = {UserVerifyUtils.ChangeEmailValidation.class})
//    private String password;
//    @UniqueEmail(/*groups = {UserVerifyUtils.ChangeEmailValidation.class}*/)
    @NotBlank
    @Email
    private String newEmail;
}
