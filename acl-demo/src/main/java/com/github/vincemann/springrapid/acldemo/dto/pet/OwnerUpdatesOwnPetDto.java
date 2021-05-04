package com.github.vincemann.springrapid.acldemo.dto.pet;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@NoArgsConstructor
@ToString(callSuper = true)
public class OwnerUpdatesOwnPetDto extends AbstractPetDto{

    @Builder
    public OwnerUpdatesOwnPetDto(Long petTypeId, LocalDate birthDate) {
        super(petTypeId, birthDate);
    }
}
