package com.github.vincemann.springrapid.auth.dto;

import jakarta.validation.constraints.NotEmpty;

public class RequestContactInformationChangeDto {
    private String oldContactInformation;
    private String newContactInformation;

    public RequestContactInformationChangeDto(String oldContactInformation, String newContactInformation) {
        this.oldContactInformation = oldContactInformation;
        this.newContactInformation = newContactInformation;
    }

    public RequestContactInformationChangeDto() {
    }

    public String getOldContactInformation() {
        return oldContactInformation;
    }

    public void setOldContactInformation(String oldContactInformation) {
        this.oldContactInformation = oldContactInformation;
    }

    public String getNewContactInformation() {
        return newContactInformation;
    }

    public void setNewContactInformation(String newContactInformation) {
        this.newContactInformation = newContactInformation;
    }

    @Override
    public String toString() {
        return "RequestContactInformationChangeDto{" +
                "oldContactInformation='" + oldContactInformation + '\'' +
                ", newContactInformation='" + newContactInformation + '\'' +
                '}';
    }
}
