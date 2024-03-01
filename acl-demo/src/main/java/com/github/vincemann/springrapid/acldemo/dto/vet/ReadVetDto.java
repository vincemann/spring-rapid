package com.github.vincemann.springrapid.acldemo.dto.vet;

import com.github.vincemann.springrapid.acldemo.dto.vet.abs.AbstractVetDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
public class ReadVetDto extends AbstractVetDto {

    private String contactInformation;
    private Set<String> roles = new HashSet<>();
    private Long id;

    @Builder
    public ReadVetDto(String firstName, String lastName, Set<Long> specialtyIds, String contactInformation, Set<String> roles, Long id) {
        super(firstName, lastName, specialtyIds);
        this.contactInformation = contactInformation;
        this.id = id;
        if (roles != null)
            this.roles = roles;
    }
}
