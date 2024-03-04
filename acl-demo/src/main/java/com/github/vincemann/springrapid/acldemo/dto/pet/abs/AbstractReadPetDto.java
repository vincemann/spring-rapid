package com.github.vincemann.springrapid.acldemo.dto.pet.abs;

import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.parent.BiDirParentId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractReadPetDto extends AbstractPetDto{
    private Long id;
    @BiDirParentId(Owner.class)
    private Long ownerId;

    public AbstractReadPetDto(String name, Long petTypeId, LocalDate birthDate, Long id, Long ownerId) {
        super(name, petTypeId, birthDate);
        this.id = id;
        this.ownerId = ownerId;
    }
}
