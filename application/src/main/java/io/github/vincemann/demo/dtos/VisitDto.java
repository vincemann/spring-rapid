package io.github.vincemann.demo.dtos;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.generic.crud.lib.config.DtoCrudControllerConfig;
import io.github.vincemann.generic.crud.lib.dto.uniDir.UniDirParentId;
import io.github.vincemann.generic.crud.lib.model.uniDir.child.UniDirChild;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class VisitDto extends IdentifiableEntityImpl<Long> implements UniDirChild {

    @Nullable
    @UniDirParentId(Pet.class)
    private Long petId;

    @NotNull
    private LocalDate date;

    @NotBlank
    @Size(max = 255)
    private String description;
}
