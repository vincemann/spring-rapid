package com.github.vincemann.springrapid.acldemo.dto.owner;

import com.github.vincemann.springrapid.acldemo.dto.owner.abs.AbstractReadOwnerDto;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OwnerReadsOwnOwnerDto extends AbstractReadOwnerDto {


    private String secret;

    @Builder
    public OwnerReadsOwnOwnerDto(String firstName, String lastName, String address, String city, String telephone, Set<String> hobbies, @Nullable Set<Long> petIds, Long id, String secret) {
        super(firstName, lastName, address, city, telephone, hobbies, petIds, id);
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "ReadOwnOwnerDto{" +
                "secret='" + secret + '\'' +
                ", id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", city='" + getCity() + '\'' +
                ", telephone='" + getTelephone() + '\'' +
                ", hobbies=" + getHobbies() +
                ", petIds=" + getPetIds() +
                '}';
    }
}


