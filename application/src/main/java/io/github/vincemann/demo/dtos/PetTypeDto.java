package io.github.vincemann.demo.dtos;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class PetTypeDto extends IdentifiableEntityImpl<Long> {
    @NotBlank
    @Size(min = 2, max = 20)
    private String name;
}
