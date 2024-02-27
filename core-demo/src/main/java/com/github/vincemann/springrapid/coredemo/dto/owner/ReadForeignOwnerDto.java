package com.github.vincemann.springrapid.coredemo.dto.owner;

import com.github.vincemann.springrapid.coredemo.dto.owner.abs.AbstractReadOwnerDto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ReadForeignOwnerDto extends AbstractReadOwnerDto {

    @Builder
    public ReadForeignOwnerDto(String address, String city, String telephone, Set<String> hobbies, Set<Long> petIds, Long id) {
        super(address, city, telephone, hobbies, petIds, id);
    }

    @Override
    public String toString() {
        return "ReadForeignOwnerDto{" +
                "city='" + city + '\'' +
                ", petIds=" + getPetIds() +
                ", address='" + getAddress() + '\'' +
                ", city='" + getCity() + '\'' +
                ", telephone='" + getTelephone() + '\'' +
                ", hobbies=" + getHobbies() +
                '}';
    }
}
