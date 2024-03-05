package com.github.vincemann.springrapid.auth.dto.user.abs;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AbstractUserDto {

    private String contactInformation;
    private Set<String> roles = new HashSet<String>();

    public AbstractUserDto(String contactInformation, Set<String> roles) {
        this.contactInformation = contactInformation;
        if (roles != null)
            this.roles = roles;
    }
}
