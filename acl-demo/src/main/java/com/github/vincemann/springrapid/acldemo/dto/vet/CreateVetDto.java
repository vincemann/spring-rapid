package com.github.vincemann.springrapid.acldemo.dto.vet;

import com.github.vincemann.springrapid.acldemo.dto.user.CreateUserDto;
import com.github.vincemann.springrapid.acldemo.dto.PersonDto;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class CreateVetDto extends PersonDto implements CreateUserDto {

    @NotBlank
    private String uuid;

    @Builder
    public CreateVetDto(@Size(min = 2, max = 20) String firstName, @Size(min = 2, max = 20) String lastName, String uuid) {
        super(firstName, lastName);
        this.uuid = uuid;
    }

    public CreateVetDto(Vet vet, String uuid){
        super(vet.getFirstName(),vet.getLastName());
        this.uuid = uuid;
    }

}
