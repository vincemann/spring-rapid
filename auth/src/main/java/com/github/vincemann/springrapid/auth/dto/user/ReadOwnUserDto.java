package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.auth.dto.user.abs.AbstractUserDto;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.sec.Roles;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ReadOwnUserDto extends AbstractUserDto {

    private String id;

    private boolean verified = false;
    private boolean blocked = false;
    private boolean admin = false;
    private boolean goodUser = false;
    //    private boolean goodAdmin = false;


    @Builder
    public ReadOwnUserDto(String contactInformation, Set<String> roles, String id) {
        super(contactInformation,roles);
        this.id = id;
        initFlags();
    }

    public void initFlags() {
        verified = !getRoles().contains(AuthRoles.UNVERIFIED);
        blocked = getRoles().contains(AuthRoles.BLOCKED);
        admin = getRoles().contains(Roles.ADMIN);
        goodUser = !(!verified || blocked);
//        goodAdmin = goodUser && admin;
    }

    @Override
    public String toString() {
        return "ReadOwnUserDto{" +
                "id='" + id + '\'' +
                ", verified=" + verified +
                ", blocked=" + blocked +
                ", admin=" + admin +
                ", goodUser=" + goodUser +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", roles=" + getRoles() +
                '}';
    }
}
