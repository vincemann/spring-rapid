package com.naturalprogrammer.spring.lemon.auth.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
/**
 * Does not contain password
 */
public class LemonReadUserDto extends AbstractLemonUserDto{

    @Builder
    public LemonReadUserDto(String email,Set<String> roles,String id) {
        super(email, roles,id);
    }
}
