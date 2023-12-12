package com.github.vincemann.springrapid.acldemo.dto.pet;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Setter
public class OwnerUpdatesPetDto extends AbstractPetDto{

    @Builder
    public OwnerUpdatesPetDto(Long petTypeId, LocalDate birthDate) {
        super(petTypeId, birthDate);
    }


    @Override
    public String toString() {
        return "OwnerUpdatesPetDto{" +
                "petTypeId=" + getPetTypeId() +
                ", birthDate=" + getBirthDate() +
                ", id=" + getId() +
                '}';
    }
}
