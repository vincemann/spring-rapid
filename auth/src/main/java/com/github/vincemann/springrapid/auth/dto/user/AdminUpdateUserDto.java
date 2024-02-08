package com.github.vincemann.springrapid.auth.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@NoArgsConstructor
@Setter
public class AdminUpdateUserDto extends AbstractUserDto {
    private String password;

    public AdminUpdateUserDto(String contactInformation, Set<String> roles, String id, String password) {
        super(contactInformation, roles, id);
        this.password = password;
    }
}
