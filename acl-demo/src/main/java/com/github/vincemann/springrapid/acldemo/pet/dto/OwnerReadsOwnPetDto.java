package com.github.vincemann.springrapid.acldemo.pet.dto;

import com.github.vincemann.springrapid.acldemo.pet.dto.abs.AbstractReadPetDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OwnerReadsOwnPetDto extends AbstractReadPetDto {

    private Set<Long> illnessIds = new HashSet<>();

    @Builder
    public OwnerReadsOwnPetDto(String name, Long petTypeId, LocalDate birthDate, Long id, Long ownerId, Set<Long> illnessIds) {
        super(name, petTypeId, birthDate, id, ownerId);
        if (illnessIds != null)
            this.illnessIds = illnessIds;
    }

    @Override
    public String toString() {
        return "OwnerReadsOwnPetDto{" +
                "illnessIds=" + illnessIds +
                ", id=" + getId() +
                ", ownerId=" + getOwnerId() +
                ", name='" + getName() + '\'' +
                ", petTypeId=" + getPetTypeId() +
                ", birthDate=" + getBirthDate() +
                '}';
    }
}
