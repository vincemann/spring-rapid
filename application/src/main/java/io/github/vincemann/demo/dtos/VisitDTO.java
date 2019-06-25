package io.github.vincemann.demo.dtos;

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
public class VisitDTO extends IdentifiableEntityImpl<Long> {
    @Nullable
    private Long petId;
    @NotNull
    private LocalDate date;
    @NotBlank
    @Size(max = 255)
    private String description;
}
