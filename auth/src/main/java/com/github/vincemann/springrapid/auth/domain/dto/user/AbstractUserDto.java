package com.github.vincemann.springrapid.auth.domain.dto.user;

import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@Setter
public abstract class AbstractUserDto extends IdentifiableEntityImpl<String> {

    private String email;
    private Set<String> roles = new HashSet<String>();

    private boolean unverified = false;
    private boolean blocked = false;
    private boolean admin = false;
    private boolean goodUser = false;
//    private boolean goodAdmin = false;

    public AbstractUserDto(String email, Set<String> roles, String id) {
        this.email = email;
        this.roles = roles;
        this.setId(id);
        initFlags();
    }

    public void initFlags() {
        unverified = roles.contains(AuthRoles.UNVERIFIED);
        blocked = roles.contains(AuthRoles.BLOCKED);
        admin = roles.contains(Roles.ADMIN);
        goodUser = !(unverified || blocked);
//        goodAdmin = goodUser && admin;
    }
}
