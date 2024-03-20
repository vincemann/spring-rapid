package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.auth.dto.user.abs.AbstractUserDto;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.sec.Roles;

import java.util.Set;


public class ReadUserDto extends AbstractUserDto {

    private String id;

    private boolean verified = false;
    private boolean blocked = false;
    private boolean admin = false;
    private boolean goodUser = false;


    public ReadUserDto(String contactInformation, Set<String> roles, String id) {
        super(contactInformation,roles);
        this.id = id;
        initFlags();
    }

    public ReadUserDto() {
    }

    public void initFlags() {
        verified = !getRoles().contains(AuthRoles.UNVERIFIED);
        blocked = getRoles().contains(AuthRoles.BLOCKED);
        admin = getRoles().contains(Roles.ADMIN);
        goodUser = !(!verified || blocked);
//        goodAdmin = goodUser && admin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isGoodUser() {
        return goodUser;
    }

    public void setGoodUser(boolean goodUser) {
        this.goodUser = goodUser;
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
