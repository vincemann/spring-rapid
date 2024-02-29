package com.github.vincemann.springrapid.acldemo.dto.owner;


import com.github.vincemann.springrapid.acldemo.dto.owner.abs.AbstractOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.abs.AbstractReadOwnerDto;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class VetReadsOwnerDto extends AbstractReadOwnerDto {

    @Builder
    public VetReadsOwnerDto(String firstName, String lastName, String address, String city, String telephone, Set<String> hobbies, @Nullable Set<Long> petIds, Long id) {
        super(firstName, lastName, address, city, telephone, hobbies, petIds, id);
    }
}
