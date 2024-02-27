package com.github.vincemann.springrapid.acldemo.dto.owner;

import com.github.vincemann.springrapid.acldemo.dto.owner.abs.AbstractOwnerDto;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter

public class ReadOwnOwnerDto extends AbstractOwnerDto {


    private String dirtySecret;


    @Builder
    public ReadOwnOwnerDto(String firstName, String lastName, String address, String city, String telephone, Set<String> hobbies, Set<Long> petIds) {
        super(firstName, lastName, address, city, telephone, hobbies, petIds);
    }
}
