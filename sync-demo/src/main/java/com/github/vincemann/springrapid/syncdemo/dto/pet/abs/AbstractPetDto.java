package com.github.vincemann.springrapid.syncdemo.dto.pet.abs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractPetDto
{

    private String name;
    private Long petTypeId;
    private Set<Long> toyIds = new HashSet<>();
    private Long ownerId;
    private LocalDate birthDate;

    public AbstractPetDto(String name, Long petTypeId, Set<Long> toyIds, Long ownerId, LocalDate birthDate) {
        this.name = name;
        this.petTypeId = petTypeId;
        if (toyIds!=null)
            this.toyIds = toyIds;
        this.ownerId = ownerId;
        this.birthDate = birthDate;
    }


}
