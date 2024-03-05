package com.github.vincemann.springrapid.authdemo.dto.user;

import com.github.vincemann.springrapid.auth.dto.user.ReadOwnUserDto;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class MyReadOwnUserDto extends ReadOwnUserDto {
    private String name;

    public MyReadOwnUserDto(String contactInformation, Set<String> roles, String id, String name) {
        super(contactInformation, roles, id);
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyReadOwnUserDto{" +
                "name='" + name + '\'' +
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
