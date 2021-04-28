package com.github.vincemann.springrapid.acldemo.dto.user;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter @ToString @NoArgsConstructor
public class AdminUpdatesUserDto {
    private String name;
    private String email;
    private String password;
    private Set<String> roles = new HashSet<String>();

    @Builder
    public AdminUpdatesUserDto(String name, String email, String password, Set<String> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
