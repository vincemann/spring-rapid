package com.github.vincemann.springrapid.acldemo.pet.dto;

import com.github.vincemann.springrapid.acldemo.pet.dto.abs.AbstractReadPetDto;
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

    @Override
    public String toString() {
        return "OwnerReadsForeignPetDto{" +
                "id=" + getId() +
                ", ownerId=" + getOwnerId() +
                ", name='" + getName() + '\'' +
                ", petTypeId=" + getPetTypeId() +
                ", birthDate=" + getBirthDate() +
                '}';
    }
}
