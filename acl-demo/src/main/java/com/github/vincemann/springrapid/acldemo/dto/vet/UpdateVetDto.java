package com.github.vincemann.springrapid.acldemo.dto.vet;

import com.github.vincemann.springrapid.acldemo.dto.vet.abs.AbstractVetDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class UpdateVetDto extends AbstractVetDto {


    @NotEmpty
    @Override
    public String getFirstName() {
        return super.getFirstName();
    }

    @NotEmpty
    @Override
    public String getLastName() {
        return super.getLastName();
    }


    @Builder
    public UpdateVetDto(String firstName, String lastName, Set<Long> specialtyIds) {
        super(firstName, lastName, specialtyIds);
    }
}
