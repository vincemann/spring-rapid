package com.github.vincemann.springrapid.authdemo.dtos.pet;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;



@NoArgsConstructor
@ToString(callSuper = true)
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
