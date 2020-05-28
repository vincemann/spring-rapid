package io.github.vincemann.springlemon.auth.domain.dto.user;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
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
