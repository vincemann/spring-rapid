package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.auth.dto.user.abs.AbstractFindUserDto;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
/**
 * Does not contain password
 */
public class FindOwnUserDto extends AbstractFindUserDto {

    @Builder
    public FindOwnUserDto(String contactInformation, Set<String> roles, String id) {
        super(contactInformation, roles,id);
    }

    @Override
    public String toString() {
        return "FindOwnUserDto{" +
                "verified=" + isVerified() +
                ", blocked=" + isBlocked() +
                ", admin=" + isAdmin() +
                ", goodUser=" + isGoodUser() +
                ", id='" + getId() + '\'' +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", roles=" + getRoles() +
                '}';
    }
}
