package com.github.vincemann.springrapid.syncdemo.dto;

import com.github.vincemann.springrapid.autobidir.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.syncdemo.model.Specialty;
import com.github.vincemann.springrapid.syncdemo.model.Vet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Validated
public class VetDto extends PersonDto  {


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
