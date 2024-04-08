package com.github.vincemann.springrapid.acldemo.vet.dto;

import com.github.vincemann.springrapid.acldemo.AbstractSignupDto;
import com.github.vincemann.springrapid.acldemo.Specialty;
import com.github.vincemann.springrapid.acldemo.vet.Vet;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class SignupVetDto extends AbstractSignupDto {

    @NotEmpty
    private Set<Long> specialtyIds = new HashSet<>();

    @Builder
    public SignupVetDto(String contactInformation, String password, String firstName, String lastName, Set<Long> specialtyIds) {
        super(contactInformation, password, firstName, lastName);
        if (specialtyIds != null)
            this.specialtyIds = specialtyIds;
    }

    public SignupVetDto(Vet vet) {
        this(vet.getContactInformation(),
                vet.getPassword(),
                vet.getFirstName(),
                vet.getLastName(),
                vet.getSpecialtys().stream().map(Specialty::getId).collect(Collectors.toSet())
        );
    }

    @Override
    public String toString() {
        return "SignupVetDto{" +
                "specialtyIds=" + specialtyIds +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                '}';
    }
}
