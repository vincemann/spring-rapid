package com.github.vincemann.springrapid.acldemo.dto.user;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter @ToString @NoArgsConstructor
public class FullUserDto {
    private String uuid;
    private String email;
    private String password;
    private Set<String> roles = new HashSet<String>();

    @Builder
    public FullUserDto(String uuid, String email, String password, Set<String> roles) {
        this.uuid = uuid;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
