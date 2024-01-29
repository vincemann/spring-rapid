package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.security.Roles;
import lombok.*;

import java.util.Set;

@Getter
@NoArgsConstructor
@Setter
public abstract class AbstractFindUserDto extends AbstractUserDto {

    private boolean verified = false;
    private boolean blocked = false;
    private boolean admin = false;
    private boolean goodUser = false;
//    private boolean goodAdmin = false;

    public AbstractFindUserDto(String contactInformation, Set<String> roles, String id) {
        super(contactInformation,roles,id);
        initFlags();
    }

    public void initFlags() {
        verified = !getRoles().contains(AuthRoles.UNVERIFIED);
        blocked = getRoles().contains(AuthRoles.BLOCKED);
        admin = getRoles().contains(Roles.ADMIN);
        goodUser = !(!verified || blocked);
//        goodAdmin = goodUser && admin;
    }
}
