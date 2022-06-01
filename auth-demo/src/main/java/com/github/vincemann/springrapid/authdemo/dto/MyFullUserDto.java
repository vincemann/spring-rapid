package com.github.vincemann.springrapid.authdemo.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter @ToString @NoArgsConstructor
public class MyFullUserDto {
    private String name;
    private String contactInformation;
    private String password;
    private Set<String> roles = new HashSet<String>();

    @Builder
    public MyFullUserDto(String name, String contactInformation, String password, Set<String> roles) {
        this.name = name;
        this.contactInformation = contactInformation;
        this.password = password;
        this.roles = roles;
    }
}
