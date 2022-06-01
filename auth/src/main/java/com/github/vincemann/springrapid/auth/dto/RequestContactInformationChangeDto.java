package com.github.vincemann.springrapid.auth.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class RequestContactInformationChangeDto {
    // no pw bc token is used for authentication
//    @Password(groups = {UserVerifyUtils.ChangeContactInformationValidation.class})
//    private String password;
//    @UniqueContactInformation(/*groups = {UserVerifyUtils.ChangeContactInformationValidation.class}*/)
    @NotBlank
//    @ContactInformation
    private String newContactInformation;
}
