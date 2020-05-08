package com.naturalprogrammer.spring.lemon.auth.domain.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
@NoArgsConstructor
@Setter
public class LemonAdminUpdateUserDto extends AbstractLemonUserDto {
    private String password;

    public LemonAdminUpdateUserDto(String email, Set<String> roles, String id, String password) {
        super(email, roles, id);
        this.password = password;
    }
}
