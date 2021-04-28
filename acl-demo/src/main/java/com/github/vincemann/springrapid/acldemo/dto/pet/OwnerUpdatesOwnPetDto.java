package com.github.vincemann.springrapid.acldemo.dto.pet;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@ToString(callSuper = true)
public class OwnerUpdatesOwnPetDto extends AbstractPetDto{

    @Builder
    public OwnerUpdatesOwnPetDto(@Size(min = 2, max = 20) String name, Long petTypeId, LocalDate birthDate) {
        super(name, petTypeId, birthDate);
    }
}
