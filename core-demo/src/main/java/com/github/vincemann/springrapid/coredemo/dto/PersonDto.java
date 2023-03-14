package com.github.vincemann.springrapid.coredemo.dto;


import com.github.vincemann.springrapid.coredemo.dto.abs.MyIdDto;
import lombok.*;

import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto extends MyIdDto<Long> {
    @Size(min = 2, max = 20)
    private String firstName;

    @Size(min = 2, max = 20)
    private String lastName;
}
