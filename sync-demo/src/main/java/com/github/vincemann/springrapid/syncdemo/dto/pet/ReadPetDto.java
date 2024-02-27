package com.github.vincemann.springrapid.syncdemo.dto.pet;

import com.github.vincemann.springrapid.coredemo.dto.pet.abs.AbstractPetDto;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.Toy;
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
    public ReadPetDto(String name, Long petTypeId, Set<Long> toyIds, Long ownerId, LocalDate birthDate, Long id) {
        super(name, petTypeId, toyIds, ownerId, birthDate);
        this.id = id;
    }

    public ReadPetDto(Pet pet){
        super(
                pet.getName(),
                pet.getPetType()==null? null: pet.getPetType().getId(),
                pet.getToys().stream().map(Toy::getId).collect(Collectors.toSet()),
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
                ", toyIds=" + getToyIds() +
                ", ownerId=" + getOwnerId() +
                ", birthDate=" + getBirthDate() +
                '}';
    }
}
