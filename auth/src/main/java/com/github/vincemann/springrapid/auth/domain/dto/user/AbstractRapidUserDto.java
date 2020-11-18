package com.github.vincemann.springrapid.auth.domain.dto.user;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@Setter
public class AbstractRapidUserDto extends IdentifiableEntityImpl<String> {

    private String email;
    private Set<String> roles = new HashSet<String>();

    public AbstractRapidUserDto(String email, Set<String> roles, String id) {
        this.email = email;
        if (roles==null){
            this.setRoles(new HashSet<>());
        }else {
            this.roles = roles;
        }
        this.setId(id);
    }
}
