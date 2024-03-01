package com.github.vincemann.springrapid.coredemo.dto.owner;

import com.github.vincemann.springrapid.coredemo.dto.owner.abs.AbstractReadOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.autobidir.resolveid.annotation.child.BiDirChildId;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ReadOwnOwnerDto extends AbstractReadOwnerDto {

    private String secret;
    private String firstName;
    private String lastName;

    @BiDirChildId(ClinicCard.class)
    private Long clinicCardId;

    @Builder
    public ReadOwnOwnerDto(String address, String city, String telephone, Set<String> hobbies, Set<Long> petIds, Long id, String secret, String firstName, String lastName, Long clinicCardId) {
        super(address, city, telephone, hobbies, petIds, id);
        this.secret = secret;
        this.firstName = firstName;
        this.lastName = lastName;
        this.clinicCardId = clinicCardId;
    }

    @Override
    public String toString() {
        return "ReadOwnOwnerDto{" +
                "dirtySecret='" + secret + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", clinicCardId=" + clinicCardId +
                ", city='" + city + '\'' +
                ", petIds=" + getPetIds() +
                ", id=" + getId() +
                ", address='" + getAddress() + '\'' +
                ", city='" + getCity() + '\'' +
                ", telephone='" + getTelephone() + '\'' +
                ", hobbies=" + getHobbies() +
                '}';
    }
}
