package com.github.vincemann.springrapid.auth.dto.user;

import com.github.vincemann.springrapid.auth.dto.user.abs.AbstractUserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@NoArgsConstructor
@Setter
public class AdminUpdateUserDto extends AbstractUserDto {
    private String password;
    private String id;

    public AdminUpdateUserDto(String contactInformation, Set<String> roles, String id, String password) {
        super(contactInformation, roles);
        this.id = id;
        this.password = password;
    }

    @Override
    public String toString() {
        return "AdminUpdateUserDto{" +
                "password='" + password + '\'' +
                ", id='" + id + '\'' +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", roles=" + getRoles() +
                '}';
    }
}
