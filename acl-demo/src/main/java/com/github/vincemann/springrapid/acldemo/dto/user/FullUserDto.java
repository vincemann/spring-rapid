package com.github.vincemann.springrapid.acldemo.dto.user;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter @ToString @NoArgsConstructor
public class FullUserDto extends IdentifiableEntityImpl<Long> {
    private String uuid;
    private String email;
    private String password;
    private Set<String> roles = new HashSet<String>();

    @Builder
    public FullUserDto(String uuid, String email, String password, Set<String> roles) {
        this.uuid = uuid;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
