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

    public static final class Builder {
        private String contactInformation;
        private String oldPassword;
        private String newPassword;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder contactInformation(String contactInformation) {
            this.contactInformation = contactInformation;
            return this;
        }

        public Builder oldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
            return this;
        }

        public Builder newPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        public ChangePasswordDto build() {
            ChangePasswordDto changePasswordDto = new ChangePasswordDto();
            changePasswordDto.setContactInformation(contactInformation);
            changePasswordDto.setOldPassword(oldPassword);
            changePasswordDto.setNewPassword(newPassword);
            return changePasswordDto;
        }
    }
}
