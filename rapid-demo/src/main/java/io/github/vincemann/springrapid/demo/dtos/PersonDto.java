package io.github.vincemann.springrapid.demo.dtos;


import lombok.*;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class PersonDto extends IdentifiableEntityImpl<Long> {
    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;
}
