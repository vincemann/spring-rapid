package com.github.vincemann.springrapid.auth.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@Setter
public class AdminUpdateRapidUserDto extends AbstractRapidUserDto {
    private String password;

    public AdminUpdateRapidUserDto(String email, Set<String> roles, String id, String password) {
        super(email, roles, id);
        this.password = password;
    }
}
