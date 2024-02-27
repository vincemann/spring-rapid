package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.dto.pet.abs.AbstractPetDto;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
public class ReadPetDto extends AbstractPetDto {

    private Long id;

    @Builder
    public ReadPetDto(String name, Long petTypeId, Long ownerId, LocalDate birthDate, Long id) {
        super(name, petTypeId, ownerId, birthDate);
        this.id = id;
    }

    public ReadPetDto(Pet pet){
        super(
                pet.getName(),
                pet.getPetType()==null? null: pet.getPetType().getId(),
                pet.getOwner()==null? null: pet.getOwner().getId(),
                pet.getBirthDate()
        );
    }
    @Override
    public String toString() {
        return "ReadPetDto{" +
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", petTypeId=" + getPetTypeId() +
                ", ownerId=" + getOwnerId() +
                ", birthDate=" + getBirthDate() +
                '}';
    }
}
