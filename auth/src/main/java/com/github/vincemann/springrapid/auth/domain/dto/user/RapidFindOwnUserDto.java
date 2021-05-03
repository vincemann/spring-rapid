package com.github.vincemann.springrapid.auth.domain.dto.user;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
/**
 * Does not contain password
 */
public class RapidFindOwnUserDto extends AbstractFindRapidUserDto {

    @Builder
    public RapidFindOwnUserDto(String email, Set<String> roles, String id) {
        super(email, roles,id);
    }
}
