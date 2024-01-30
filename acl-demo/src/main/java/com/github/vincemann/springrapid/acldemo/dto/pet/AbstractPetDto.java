package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.PetType;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.core.dto.IdAwareDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractPetDto extends IdAwareDto<Long>
         {

    public AbstractPetDto(Long petTypeId, LocalDate birthDate) {
        this.petTypeId = petTypeId;
        this.birthDate = birthDate;
    }


    @UniDirChildId(PetType.class)
    private Long petTypeId;
    private LocalDate birthDate;

    public AbstractPetDto(Pet pet){
        this.petTypeId = pet.getPetType() == null ? null : pet.getPetType().getId();
        this.birthDate = pet.getBirthDate();
    }
}
