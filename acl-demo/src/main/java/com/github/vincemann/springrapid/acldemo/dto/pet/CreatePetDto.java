package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.dto.pet.abs.AbstractPetDto;
import com.github.vincemann.springrapid.acldemo.model.Pet;
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
    public CreatePetDto(String name, Long petTypeId, Long ownerId, LocalDate birthDate) {
        super(name, petTypeId, ownerId, birthDate);
    }

    public CreatePetDto(Pet pet){
        super(
                pet.getName(),
                pet.getPetType()==null? null: pet.getPetType().getId(),
                pet.getOwner()==null? null: pet.getOwner().getId(),
                pet.getBirthDate()
        );
    }

    @Override
    public String toString() {
        return "CreatePetDto{" +
                "name='" + getName() + '\'' +
                ", petTypeId=" + getPetTypeId() +
                ", ownerId=" + getOwnerId() +
                ", birthDate=" + getBirthDate() +
                '}';
    }
}
