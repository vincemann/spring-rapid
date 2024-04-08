package com.github.vincemann.springrapid.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;


public class ResetPasswordDto implements Serializable {
    @NotEmpty
    String newPassword;

    @NotEmpty
    String code;

    public ResetPasswordDto(String newPassword, String code) {
        this.newPassword = newPassword;
        this.code = code;
    }

    public ResetPasswordDto() {
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ResetPasswordDto{" +
                "newPassword='" + newPassword + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
