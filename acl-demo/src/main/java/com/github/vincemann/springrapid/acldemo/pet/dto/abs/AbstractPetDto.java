package com.github.vincemann.springrapid.acldemo.pet.dto.abs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractPetDto
{

    private String name;
    private Long petTypeId;
    private LocalDate birthDate;

    public AbstractPetDto(String name, Long petTypeId, LocalDate birthDate) {
        this.name = name;
        this.petTypeId = petTypeId;
        this.birthDate = birthDate;
    }


}
