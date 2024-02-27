package com.github.vincemann.springrapid.acldemo.dto.vet;

import com.github.vincemann.springrapid.acldemo.dto.PersonDto;
import com.github.vincemann.springrapid.acldemo.model.Specialty;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildIdCollection;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Validated

public class FullVetDto  {


    @Builder
    public FullVetDto( String firstName, @Size(min = 2, max = 20) String lastName, Set<Long> specialtyIds) {
        super(firstName, lastName);
        if(specialtyIds!=null)
            this.specialtyIds = specialtyIds;
    }

    public FullVetDto(Vet vet){
        super(vet.getFirstName(),vet.getLastName());
    }

    @BiDirChildIdCollection(Specialty.class)
    private Set<Long> specialtyIds = new HashSet<>();
}
