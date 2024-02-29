package com.github.vincemann.springrapid.acldemo.dto.owner;

import com.github.vincemann.springrapid.acldemo.dto.owner.abs.AbstractOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.abs.AbstractReadOwnerDto;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter

public class ReadOwnOwnerDto extends AbstractReadOwnerDto {


    private String dirtySecret;

    @Builder
    public ReadOwnOwnerDto(String firstName, String lastName, String address, String city, String telephone, Set<String> hobbies, @Nullable Set<Long> petIds, Long id, String dirtySecret) {
        super(firstName, lastName, address, city, telephone, hobbies, petIds, id);
        this.dirtySecret = dirtySecret;
    }
}
