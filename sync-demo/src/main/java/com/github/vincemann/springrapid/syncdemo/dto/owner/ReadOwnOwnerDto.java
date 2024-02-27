package com.github.vincemann.springrapid.syncdemo.dto.owner;

import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildId;
import com.github.vincemann.springrapid.syncdemo.dto.owner.abs.AbstractReadOwnerDto;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ReadOwnOwnerDto extends AbstractReadOwnerDto {

    private String dirtySecret;
    private String firstName;
    private String lastName;

    @BiDirChildId(ClinicCard.class)
    private Long clinicCardId;

    @Builder
    public ReadOwnOwnerDto(String address, String city, String telephone, Set<String> hobbies, Set<Long> petIds, Long id, String dirtySecret, String firstName, String lastName, Long clinicCardId) {
        super(address, city, telephone, hobbies, petIds, id);
        this.dirtySecret = dirtySecret;
        this.firstName = firstName;
        this.lastName = lastName;
        this.clinicCardId = clinicCardId;
    }

    @Override
    public String toString() {
        return "ReadOwnOwnerDto{" +
                "dirtySecret='" + dirtySecret + '\'' +
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
