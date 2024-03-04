package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.dto.pet.abs.AbstractReadPetDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class OwnerReadsForeignPetDto extends AbstractReadPetDto {

    @Builder
    public OwnerReadsForeignPetDto(String name, Long petTypeId, LocalDate birthDate, Long id, Long ownerId) {
        super(name, petTypeId, birthDate, id, ownerId);
    }
}
