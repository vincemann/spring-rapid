package com.github.vincemann.springrapid.acldemo.owner.dto;


import com.github.vincemann.springrapid.acldemo.owner.dto.abs.AbstractReadOwnerDto;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class VetReadsOwnerDto extends AbstractReadOwnerDto {

    private Set<Long> petIds = new HashSet<>();

    @Builder
    public VetReadsOwnerDto(String firstName, String lastName, String address, String city, String telephone, Set<String> hobbies, @Nullable Set<Long> petIds, Long id) {
        super(firstName, lastName, address, city, telephone, hobbies, id);
        if (petIds != null)
            this.petIds = petIds;
    }
}
