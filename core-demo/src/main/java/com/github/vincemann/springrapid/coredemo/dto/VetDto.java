package com.github.vincemann.springrapid.coredemo.dto;

import com.github.vincemann.springrapid.coredemo.dto.abs.IdAwareDto;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.child.BiDirChildIdCollection;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@Validated
public class VetDto extends IdAwareDto {


    @NotEmpty
    @Size(min = 2, max = 20)
    private String firstName;
    @NotEmpty
    @Size(min = 2, max = 20)
    private String lastName;
    @BiDirChildIdCollection(Specialty.class)
    private Set<Long> specialtyIds = new HashSet<>();

    @Builder
    public VetDto(String firstName, String lastName,Set<Long> specialtyIds, Long id) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        if(specialtyIds!=null)
            this.specialtyIds = specialtyIds;
    }

    public VetDto(Vet vet){
        this(vet.getFirstName(),vet.getLastName(),vet.getSpecialtys().stream().map(Specialty::getId).collect(Collectors.toSet()), vet.getId());
    }

    @Override
    public String toString() {
        return "VetDto{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", specialtyIds=" + specialtyIds +
                ", id=" + getId() +
                '}';
    }
}
