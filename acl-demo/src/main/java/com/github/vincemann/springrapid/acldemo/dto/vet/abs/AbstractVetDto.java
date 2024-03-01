package com.github.vincemann.springrapid.acldemo.dto.vet.abs;

import com.github.vincemann.springrapid.acldemo.model.Specialty;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.child.BiDirChildIdCollection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class AbstractVetDto {
    private String firstName;
    private String lastName;

    @BiDirChildIdCollection(Specialty.class)
    private Set<Long> specialtyIds = new HashSet<>();

    public AbstractVetDto(String firstName, String lastName, Set<Long> specialtyIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        if (specialtyIds != null)
            this.specialtyIds = specialtyIds;
    }
}
