package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.security.Roles;
import lombok.*;

import java.util.Set;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@Setter
public abstract class AbstractFindRapidUserDto extends AbstractRapidUserDto {

    private boolean unverified = false;
    private boolean blocked = false;
    private boolean admin = false;
    private boolean goodUser = false;
//    private boolean goodAdmin = false;

    public AbstractFindRapidUserDto(String email, Set<String> roles, String id) {
        super(email,roles,id);
        initFlags();
    }

    public void initFlags() {
        unverified = getRoles().contains(AuthRoles.UNVERIFIED);
        blocked = getRoles().contains(AuthRoles.BLOCKED);
        admin = getRoles().contains(Roles.ADMIN);
        goodUser = !(unverified || blocked);
//        goodAdmin = goodUser && admin;
    }
}
