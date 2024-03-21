package com.github.vincemann.springrapid.authdemo.dto.abs;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractUserDto {
    private String name;
    private String contactInformation;
    private Set<String> roles = new HashSet<String>();

    public AbstractUserDto(Set<String> roles, Float rating, Set<Long> schoolIds, String name) {
        this.contactInformation = contactInformation;
        if (roles != null)
            this.roles = roles;
        this.name = name;
    }
}
