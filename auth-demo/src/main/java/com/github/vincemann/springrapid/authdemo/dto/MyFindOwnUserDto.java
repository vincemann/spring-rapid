package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.auth.dto.user.RapidFindOwnUserDto;
import lombok.*;

import java.util.Set;

@Getter
@Setter

@NoArgsConstructor
public class MyFindOwnUserDto extends RapidFindOwnUserDto {
    private String name;

    public MyFindOwnUserDto(String contactInformation, Set<String> roles, String id, String name) {
        super(contactInformation, roles, id);
        this.name = name;
    }
}
