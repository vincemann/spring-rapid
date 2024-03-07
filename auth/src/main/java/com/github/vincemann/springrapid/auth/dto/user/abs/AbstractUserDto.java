package com.github.vincemann.springrapid.auth.dto.user.abs;



import java.util.HashSet;
import java.util.Set;

public class AbstractUserDto {

    private String contactInformation;
    private Set<String> roles = new HashSet<String>();

    public AbstractUserDto(String contactInformation, Set<String> roles) {
        this.contactInformation = contactInformation;
        if (roles != null)
            this.roles = roles;
    }

    public AbstractUserDto() {
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
