package com.github.vincemann.springrapid.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
public class SignupAdminDto {

    @NotBlank
    private String contactInformation;

    @NotBlank
    private String password;
}
