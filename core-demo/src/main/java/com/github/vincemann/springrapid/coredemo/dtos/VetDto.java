package com.github.vincemann.springrapid.coredemo.dtos;

import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.BiDirParentDto;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Validated
@ToString(callSuper = true)
public class VetDto extends PersonDto implements BiDirParentDto {


    @Builder
    public VetDto(@Size(min = 2, max = 20) String firstName, @Size(min = 2, max = 20) String lastName,Set<Long> specialtyIds) {
        super(firstName, lastName);
        if(specialtyIds!=null)
            this.specialtyIds = specialtyIds;
    }

    public VetDto(Vet vet){
        super(vet.getFirstName(),vet.getLastName());
    }

    @BiDirChildIdCollection(Specialty.class)
    private Set<Long> specialtyIds = new HashSet<>();
}
