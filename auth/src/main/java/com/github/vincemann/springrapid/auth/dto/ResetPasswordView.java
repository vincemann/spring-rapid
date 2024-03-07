package com.github.vincemann.springrapid.auth.dto;

public class ResetPasswordView {
    private String password;
    private String matchPassword;

    public ResetPasswordView(String password, String matchPassword) {
        this.password = password;
        this.matchPassword = matchPassword;
    }

    public ResetPasswordView() {
    }

    @Override
    public String toString() {
        return "ResetPasswordView{" +
                "password='" + password + '\'' +
                ", matchPassword='" + matchPassword + '\'' +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchPassword() {
        return matchPassword;
    }

    public void setMatchPassword(String matchPassword) {
        this.matchPassword = matchPassword;
    }
}
