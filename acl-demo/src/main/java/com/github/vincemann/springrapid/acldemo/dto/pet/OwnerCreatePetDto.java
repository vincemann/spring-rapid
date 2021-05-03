package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.BiDirParentDto;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;
import lombok.*;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class OwnerCreatePetDto extends AbstractPetDto implements BiDirParentDto {

    @BiDirParentId(Owner.class)
    private Long ownerId;

    @Builder
    public OwnerCreatePetDto(@Size(min = 2, max = 20) String name, Long petTypeId, LocalDate birthDate, Long ownerId) {
        super(name, petTypeId, birthDate);
        this.ownerId = ownerId;
    }

    public OwnerCreatePetDto(Pet pet, Long ownerId) {
        super(pet);
        this.ownerId = ownerId;
    }
}
