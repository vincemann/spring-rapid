package com.github.vincemann.springrapid.coredemo.dtos.pet;

import com.github.vincemann.springrapid.coredemo.model.Owner;

import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.UniDirParentDto;
import lombok.*;
import org.springframework.lang.Nullable;
import com.github.vincemann.springrapid.entityrelationship.dto.child.BiDirChildDto;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public abstract class AbstractPetDto extends IdentifiableEntityImpl<Long>
        implements UniDirParentDto, BiDirChildDto {

    @Size(min = 2, max = 20)
    private String name;

    @UniDirChildId(PetType.class)
    private Long petTypeId;

    @Nullable
    @BiDirParentId(Owner.class)
    private Long ownerId;

    @Nullable
    private LocalDate birthDate;
}
