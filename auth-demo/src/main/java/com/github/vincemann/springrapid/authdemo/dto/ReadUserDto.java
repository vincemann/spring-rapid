package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.auth.Roles;
import com.github.vincemann.springrapid.authdemo.dto.abs.AbstractUserDto;
import com.github.vincemann.springrapid.auth.Roles;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ReadUserDto extends AbstractUserDto {
    private Long id;
    private boolean verified = false;
    private boolean blocked = false;
    private boolean admin = false;
    private boolean goodUser = false;

    public ReadUserDto(Set<String> roles,String contactInformation, String name, Long id) {
        super(roles,contactInformation, name);
        this.id = id;
        initFlags();
    }

    public void initFlags() {
        verified = !getRoles().contains(Roles.UNVERIFIED);
        blocked = getRoles().contains(Roles.BLOCKED);
        admin = getRoles().contains(Roles.ADMIN);
        goodUser = !(!verified || blocked);
//        goodAdmin = goodUser && admin;
    }

    @Override
    public String toString() {
        return "MyReadOwnUserDto{" +
                "name='" + getName() + '\'' +
                ", id='" + getId() + '\'' +
                ", verified=" + isVerified() +
                ", blocked=" + isBlocked() +
                ", admin=" + isAdmin() +
                ", goodUser=" + isGoodUser() +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", roles=" + getRoles() +
                '}';
    }
}
