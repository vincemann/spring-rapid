package com.github.vincemann.springrapid.coredemo.dtos.pet;

import com.github.vincemann.springrapid.coredemo.model.Pet;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;



@NoArgsConstructor
@ToString(callSuper = true)
public class PetDto extends AbstractPetDto {



    @NotBlank
    @Override
    public @Size(min = 2, max = 20) String getName() {
        return super.getName();
    }


    @Builder
    public PetDto(@NotBlank @Size(min = 2, max = 20) String name, Long petTypeId, Long ownerId, LocalDate birthDate) {
        super(name, petTypeId, ownerId, birthDate);
    }

    public PetDto(Pet pet){
        super(
                pet.getName(),
                pet.getPetType()==null? null: pet.getPetType().getId(),
                pet.getOwner()==null? null: pet.getOwner().getId(),
                pet.getBirthDate()
        );
    }
}
