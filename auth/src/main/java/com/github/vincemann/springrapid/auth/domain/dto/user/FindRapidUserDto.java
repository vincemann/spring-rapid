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
public class FindRapidUserDto extends AbstractFindRapidUserDto {

    @Builder
    public FindRapidUserDto(String email, Set<String> roles, String id) {
        super(email, roles,id);
    }
}
