package com.github.vincemann.springrapid.auth.service;

import javax.validation.constraints.NotBlank;

public class ChangePasswordDto {

    String contactInformation;
    @NotBlank
    String oldPassword;
    @NotBlank
    String newPassword;
    @NotBlank
    String retypeNewPassword;
}
