package com.github.vincemann.springrapid.auth.dto;

public class ChangePasswordDto {
    String contactInformation;
    String oldPassword;
    String newPassword;

    public ChangePasswordDto(String contactInformation, String oldPassword, String newPassword) {
        this.contactInformation = contactInformation;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public ChangePasswordDto() {
    }

    @Override
    public String toString() {
        return "ChangePasswordDto{" +
                "contactInformation='" + contactInformation + '\'' +
                ", oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
