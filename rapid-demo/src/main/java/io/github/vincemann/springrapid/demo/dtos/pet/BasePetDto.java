package io.github.vincemann.springrapid.demo.dtos.pet;

import io.github.vincemann.springrapid.demo.dtos.pet.abs.AbstractPetDto;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;



@NoArgsConstructor
public class BasePetDto extends AbstractPetDto {



    @NotBlank
    @Override
    public @Size(min = 2, max = 20) String getName() {
        return super.getName();
    }

    @NotNull
    @Override
    public Long getPetTypeId() {
        return super.getPetTypeId();
    }

    @Builder
    public BasePetDto(@NotBlank @Size(min = 2, max = 20) String name, Long petTypeId, Long ownerId, LocalDate birthDate) {
        super(name, petTypeId, ownerId, birthDate);
    }
}
