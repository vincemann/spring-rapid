package com.github.vincemann.springrapid.auth.dto;

public class LoginDto {
    private String contactInformation;
    private String password;

    public LoginDto(String contactInformation, String password) {
        this.contactInformation = contactInformation;
        this.password = password;
    }

    public LoginDto() {
    }

    @Override
    public String toString() {
        return "LoginDto{" +
                "contactInformation='" + contactInformation + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
