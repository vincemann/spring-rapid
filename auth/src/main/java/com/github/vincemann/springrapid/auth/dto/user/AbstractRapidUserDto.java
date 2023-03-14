package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.core.dto.AbstractIdDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@Setter
public class AbstractRapidUserDto extends AbstractIdDto<String> {

    private String contactInformation;
    private Set<String> roles = new HashSet<String>();

    public AbstractRapidUserDto(String contactInformation, Set<String> roles, String id) {
        this.contactInformation = contactInformation;
        if (roles==null){
            this.setRoles(new HashSet<>());
        }else {
            this.roles = roles;
        }
        this.setId(id);
    }
}
