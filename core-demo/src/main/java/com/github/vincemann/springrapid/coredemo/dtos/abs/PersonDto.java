package com.github.vincemann.springrapid.coredemo.dtos.abs;


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
    @Size(min = 2, max = 20)
    private String firstName;

    @Size(min = 2, max = 20)
    private String lastName;
}
