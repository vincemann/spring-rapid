package com.github.vincemann.springrapid.acldemo.vet.dto.abs;


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
    private Set<Long> specialtyIds = new HashSet<>();

    public AbstractVetDto(String firstName, String lastName, Set<Long> specialtyIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        if (specialtyIds != null)
            this.specialtyIds = specialtyIds;
    }
}
