package io.github.vincemann.demo.dtos;

import io.github.vincemann.demo.model.Owner;

import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirChildId;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirDtoParent;
import lombok.*;
import org.springframework.lang.Nullable;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirDtoChild;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PetDto extends IdentifiableEntityImpl<Long> implements UniDirDtoParent, BiDirDtoChild {
    @NotBlank
    @Size(min = 2, max = 20)
    private String name;

    @NotNull
    @UniDirChildId(PetType.class)
    private Long petTypeId;

    @Nullable
    @BiDirParentId(Owner.class)
    private Long ownerId;

    @Nullable
    private LocalDate birthDate;
}
