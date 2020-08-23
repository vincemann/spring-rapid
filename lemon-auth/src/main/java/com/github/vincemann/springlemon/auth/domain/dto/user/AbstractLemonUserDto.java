package com.github.vincemann.springlemon.auth.domain.dto.user;

import com.github.vincemann.springlemon.auth.domain.LemonRole;
import com.github.vincemann.springrapid.core.security.RapidRole;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@Setter
public abstract class AbstractLemonUserDto extends IdentifiableEntityImpl<String> {

    private String email;
    private Set<String> roles = new HashSet<String>();

    private boolean unverified = false;
    private boolean blocked = false;
    private boolean admin = false;
    private boolean goodUser = false;
    private boolean goodAdmin = false;

    public AbstractLemonUserDto(String email, Set<String> roles,String id) {
        this.email = email;
        this.roles = roles;
        this.setId(id);
        initialize();
    }

    public void initialize() {
        unverified = roles.contains(LemonRole.UNVERIFIED);
        blocked = roles.contains(LemonRole.BLOCKED);
        admin = roles.contains(RapidRole.ADMIN);
        goodUser = !(unverified || blocked);
        goodAdmin = goodUser && admin;
    }
}
