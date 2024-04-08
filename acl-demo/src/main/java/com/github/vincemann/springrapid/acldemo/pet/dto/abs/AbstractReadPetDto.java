package com.github.vincemann.springrapid.acldemo.pet.dto.abs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractReadPetDto extends AbstractPetDto{
    private Long id;
    private Long ownerId;

    public AbstractReadPetDto(String name, Long petTypeId, LocalDate birthDate, Long id, Long ownerId) {
        super(name, petTypeId, birthDate);
        this.id = id;
        this.ownerId = ownerId;
    }
}
