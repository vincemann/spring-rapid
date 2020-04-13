package io.github.vincemann.springrapid.demo.dtos.pet;

import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@NoArgsConstructor
public class UpdatePetDto extends AbstractPetDto {

    public UpdatePetDto(@NotBlank @Size(min = 2, max = 20) String name, Long petTypeId, Long ownerId, LocalDate birthDate) {
        super(name, petTypeId, ownerId, birthDate);
    }
}
