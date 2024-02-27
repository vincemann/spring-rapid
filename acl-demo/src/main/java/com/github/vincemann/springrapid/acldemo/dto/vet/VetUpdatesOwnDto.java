package com.github.vincemann.springrapid.acldemo.dto.vet;

import com.github.vincemann.springrapid.acldemo.dto.PersonDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.abs.AbstractVetDto;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Setter
@NoArgsConstructor
public class VetUpdatesOwnDto extends AbstractVetDto {

    @Builder
    public VetUpdatesOwnDto(@Size(min = 2, max = 20) String firstName, @Size(min = 2, max = 20) String lastName) {
        super(firstName, lastName);
    }
}
