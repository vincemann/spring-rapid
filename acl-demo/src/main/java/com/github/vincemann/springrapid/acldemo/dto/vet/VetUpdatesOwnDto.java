package com.github.vincemann.springrapid.acldemo.dto.vet;

import com.github.vincemann.springrapid.acldemo.dto.PersonDto;
import lombok.Builder;

import javax.validation.constraints.Size;

public class VetUpdatesOwnDto extends PersonDto {

    @Builder
    public VetUpdatesOwnDto(@Size(min = 2, max = 20) String firstName, @Size(min = 2, max = 20) String lastName) {
        super(firstName, lastName);
    }
}
