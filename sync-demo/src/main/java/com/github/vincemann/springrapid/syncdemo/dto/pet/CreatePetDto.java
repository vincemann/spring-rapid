package com.github.vincemann.springrapid.syncdemo.dto.pet;

import com.github.vincemann.springrapid.coredemo.dto.pet.abs.AbstractPetDto;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.model.Toy;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CreatePetDto extends AbstractPetDto {

    @NotBlank
    @Size(min = 2, max = 20)
    @Override
    public  String getName() {
        return super.getName();
    }

    @Positive
    @Override
    public Long getPetTypeId() {
        return super.getPetTypeId();
    }

    @Positive
    @Override
    public Long getOwnerId() {
        return super.getOwnerId();
    }

    @Builder
    public CreatePetDto(String name, Long petTypeId, Set<Long> toyIds, Long ownerId, LocalDate birthDate) {
        super(name, petTypeId, toyIds, ownerId, birthDate);
    }

    public CreatePetDto(Pet pet){
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
        return "CreatePetDto{" +
                "name='" + getName() + '\'' +
                ", petTypeId=" + getPetTypeId() +
                ", toyIds=" + getToyIds() +
                ", ownerId=" + getOwnerId() +
                ", birthDate=" + getBirthDate() +
                '}';
    }
}
