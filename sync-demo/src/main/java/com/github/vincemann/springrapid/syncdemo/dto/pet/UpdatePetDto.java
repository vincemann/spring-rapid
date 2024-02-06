package com.github.vincemann.springrapid.syncdemo.dto.pet;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;


@NoArgsConstructor
public class UpdatePetDto extends AbstractPetDto {

    @Builder
    public UpdatePetDto(@Size(min = 2, max = 20) String name, Long petTypeId, Set<Long> toyIds, Long ownerId, LocalDate birthDate) {
        super(name, petTypeId, toyIds, ownerId, birthDate);
    }
}
