package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.PetType;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.UniDirParentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public abstract class AbstractPetDto extends IdentifiableEntityImpl<Long>
        implements UniDirParentDto {

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
