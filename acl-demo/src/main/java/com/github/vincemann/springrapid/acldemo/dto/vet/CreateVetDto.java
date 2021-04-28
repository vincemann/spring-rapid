package com.github.vincemann.springrapid.acldemo.dto.vet;

import com.github.vincemann.springrapid.acldemo.dto.abs.CreateUserDto;
import com.github.vincemann.springrapid.acldemo.dto.abs.PersonDto;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Size;

@Getter
public class CreateVetDto extends PersonDto implements CreateUserDto {

    @Builder
    public CreateVetDto(@Size(min = 2, max = 20) String firstName, @Size(min = 2, max = 20) String lastName, String uuid) {
        super(firstName, lastName);
        this.uuid = uuid;
    }

    private String uuid;
}
