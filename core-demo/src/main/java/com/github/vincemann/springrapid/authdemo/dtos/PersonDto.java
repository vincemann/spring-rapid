package com.github.vincemann.springrapid.authdemo.dtos;


import lombok.*;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@AllArgsConstructor
public class PersonDto extends IdentifiableEntityImpl<Long> {
    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;
}
