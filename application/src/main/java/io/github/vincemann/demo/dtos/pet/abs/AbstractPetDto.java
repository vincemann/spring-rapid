package io.github.vincemann.demo.dtos.pet.abs;

import io.github.vincemann.demo.model.Owner;

import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirChildId;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirParentDto;
import lombok.*;
import org.springframework.lang.Nullable;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildDto;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public abstract class AbstractPetDto extends IdentifiableEntityImpl<Long> implements UniDirParentDto, BiDirChildDto {

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
