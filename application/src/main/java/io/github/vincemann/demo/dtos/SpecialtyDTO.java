package io.github.vincemann.demo.dtos;


import lombok.*;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class SpecialtyDTO extends IdentifiableEntityImpl<Long> {
    @NotBlank
    @Size(min = 2, max = 255)
    private String description;
}
