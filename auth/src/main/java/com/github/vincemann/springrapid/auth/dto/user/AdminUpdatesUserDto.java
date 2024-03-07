package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.auth.dto.user.abs.AbstractUserDto;


import java.util.Set;


public class AdminUpdatesUserDto extends AbstractUserDto {
    private String password;
    private String id;

    public AdminUpdatesUserDto(String contactInformation, Set<String> roles, String id, String password) {
        super(contactInformation, roles);
        this.id = id;
        this.password = password;
    }

    public AdminUpdatesUserDto() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AdminUpdateUserDto{" +
                "password='" + password + '\'' +
                ", id='" + id + '\'' +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", roles=" + getRoles() +
                '}';
    }
}
