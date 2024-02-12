package com.github.vincemann.springrapid.acldemo.dto.user;

import com.github.vincemann.springrapid.core.dto.IdAwareDto;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter @ToString @NoArgsConstructor
public class MyFullUserDto extends IdAwareDto<Long> {
    private String uuid;
    private String contactInformation;
    private String password;
    private Set<String> roles = new HashSet<String>();

    @Builder
    public MyFullUserDto(String uuid, String contactInformation, String password, Set<String> roles) {
        this.uuid = uuid;
        this.contactInformation = contactInformation;
        this.password = password;
        this.roles = roles;
    }
}
