package com.github.vincemann.springrapid.auth.dto.user;

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
}
