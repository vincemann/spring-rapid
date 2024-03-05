package com.github.vincemann.springrapid.authdemo.dto.user;

import com.github.vincemann.springrapid.auth.dto.user.AdminUpdatesUserDto;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class MyAdminUpdatesUserDto extends AdminUpdatesUserDto {
    private String name;
    private String contactInformation;
    private String password;
    private Set<String> roles = new HashSet<String>();

    @Builder(builderMethodName = "Builder")
    public MyAdminUpdatesUserDto(String name, String contactInformation, String password, Set<String> roles) {
        this.name = name;
        this.contactInformation = contactInformation;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "MyAdminUpdatesUserDto{" +
                "name='" + name + '\'' +
                ", contactInformation='" + contactInformation + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                ", id='" + getId() + '\'' +
                '}';
    }
}
