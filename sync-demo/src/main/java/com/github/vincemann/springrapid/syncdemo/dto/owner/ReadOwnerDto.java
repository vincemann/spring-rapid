package com.github.vincemann.springrapid.syncdemo.dto.owner;

import com.github.vincemann.springrapid.syncdemo.dto.owner.abs.AbstractOwnerDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ReadOwnerDto extends AbstractOwnerDto {

    private String dirtySecret;
    private String firstName;
    private String lastName;
    private Long clinicCardId;
    private Set<Long> petIds = new HashSet<>();
    private Long id;

    @Builder
    public ReadOwnerDto(String address, String city, String telephone, Set<String> hobbies, Set<Long> petIds, Long id, String dirtySecret, String firstName, String lastName, Long clinicCardId) {
        super(address, city, telephone, hobbies);
        if (petIds != null)
            this.petIds = petIds;
        this.id = id;
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
