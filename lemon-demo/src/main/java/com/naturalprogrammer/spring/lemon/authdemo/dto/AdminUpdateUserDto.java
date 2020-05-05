package com.naturalprogrammer.spring.lemon.authdemo.dto;

import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonUserDto;
import lombok.*;

import java.util.Set;

@Getter @Setter @ToString @AllArgsConstructor
public class AdminUpdateUserDto extends LemonUserDto {
    private String name;

    @Builder(builderMethodName = "Builder")
    public AdminUpdateUserDto(String email, String password, Set<String> roles, boolean unverified, boolean blocked, boolean admin, boolean goodUser, boolean goodAdmin, String name) {
        super(email, password, roles, unverified, blocked, admin, goodUser, goodAdmin);
        this.name = name;
    }
}
