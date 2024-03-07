package com.github.vincemann.springrapid.auth.dto;


public class ChangeContactInformationView {
    private String newContactInformation;

    public ChangeContactInformationView(String newContactInformation) {
        this.newContactInformation = newContactInformation;
    }

    public ChangeContactInformationView() {
    }

    @Override
    public String toString() {
        return "ChangeContactInformationView{" +
                "newContactInformation='" + newContactInformation + '\'' +
                '}';
    }

    public String getNewContactInformation() {
        return newContactInformation;
    }

    public void setNewContactInformation(String newContactInformation) {
        this.newContactInformation = newContactInformation;
    }
}
