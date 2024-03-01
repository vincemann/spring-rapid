package com.github.vincemann.springrapid.acldemo.dto.pet.abs;

import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.PetType;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.child.UniDirChildId;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.parent.BiDirParentId;
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

    @UniDirChildId(PetType.class)
    private Long petTypeId;

    private LocalDate birthDate;

    public AbstractPetDto(String name, Long petTypeId, LocalDate birthDate) {
        this.name = name;
        this.petTypeId = petTypeId;
        this.birthDate = birthDate;
    }


}
