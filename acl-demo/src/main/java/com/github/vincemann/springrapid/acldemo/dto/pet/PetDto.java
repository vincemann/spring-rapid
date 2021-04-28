package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.Toy;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;


@NoArgsConstructor
@ToString(callSuper = true)
public class PetDto extends AbstractPetDto {



    @NotBlank
    @Override
    public @Size(min = 2, max = 20) String getName() {
        return super.getName();
    }


    @Builder
    public PetDto(@Size(min = 2, max = 20) String name, Long petTypeId, Set<Long> toyIds, Long ownerId, LocalDate birthDate) {
        super(name, petTypeId, toyIds, ownerId, birthDate);
    }

    public PetDto(Pet pet){
        super(
                pet.getName(),
                pet.getPetType()==null? null: pet.getPetType().getId(),
                pet.getToys().stream().map(Toy::getId).collect(Collectors.toSet()),
                pet.getOwner()==null? null: pet.getOwner().getId(),
                pet.getBirthDate()
        );
    }
}
