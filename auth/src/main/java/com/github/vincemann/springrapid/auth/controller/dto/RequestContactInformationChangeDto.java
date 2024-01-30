package com.github.vincemann.springrapid.auth.controller.dto;

import lombok.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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