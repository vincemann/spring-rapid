package com.github.vincemann.springrapid.auth.dto;



import java.io.Serializable;


public class SignupDto implements Serializable {

    private String contactInformation;
    private String password;


    public SignupDto(String contactInformation, String password) {
        this.contactInformation = contactInformation;
        this.password = password;
    }

    public SignupDto() {
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

    @Override
    public String toString() {
        return "SignupDto{" +
                "contactInformation='" + contactInformation + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
