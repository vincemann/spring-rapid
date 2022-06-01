package com.github.vincemann.springrapid.auth.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@NoArgsConstructor
@Setter
public class AdminUpdateRapidUserDto extends AbstractRapidUserDto {
    private String password;

    public AdminUpdateRapidUserDto(String contactInformation, Set<String> roles, String id, String password) {
        super(contactInformation, roles, id);
        this.password = password;
    }
}
