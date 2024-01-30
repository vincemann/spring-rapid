package com.github.vincemann.springrapid.acldemo.dto;


import com.github.vincemann.springrapid.core.dto.IdAwareDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor

@AllArgsConstructor
public class PersonDto extends IdAwareDto<Long> {
    @Size(min = 2, max = 20)
    private String firstName;

    @Size(min = 2, max = 20)
    private String lastName;
}
