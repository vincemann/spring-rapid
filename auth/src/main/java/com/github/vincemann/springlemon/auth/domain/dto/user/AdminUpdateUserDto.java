package com.github.vincemann.springlemon.auth.domain.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@Setter
public class AdminUpdateUserDto extends AbstractUserDto {
    private String password;

    public AdminUpdateUserDto(String email, Set<String> roles, String id, String password) {
        super(email, roles, id);
        this.password = password;
    }
}
