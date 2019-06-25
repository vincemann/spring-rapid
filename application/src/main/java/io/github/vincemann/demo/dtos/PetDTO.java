package io.github.vincemann.demo.dtos;

import io.github.vincemann.demo.model.Owner;

import lombok.*;
import org.springframework.lang.Nullable;
import io.github.vincemann.generic.crud.lib.bidir.BiDirDTOChild;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParentId;

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
public class PetDTO extends IdentifiableEntityImpl<Long> implements BiDirDTOChild<Long> {
    @NotBlank
    @Size(min = 2, max = 20)
    private String name;
    @NotNull
    private PetTypeDTO petType;
    @Nullable
    @BiDirParentId(Owner.class)
    private Long ownerId;
    @Nullable
    private LocalDate birthDate;
}
