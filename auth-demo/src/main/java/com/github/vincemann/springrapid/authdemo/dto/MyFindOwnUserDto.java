package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.auth.dto.user.RapidFindOwnUserDto;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class MyFindOwnUserDto extends RapidFindOwnUserDto {
    private String name;

    public MyFindOwnUserDto(String email, Set<String> roles, String id, String name) {
        super(email, roles, id);
        this.name = name;
    }
}
