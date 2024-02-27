package com.github.vincemann.springrapid.coredemo.dto.owner;

import com.github.vincemann.springrapid.coredemo.dto.owner.abs.AbstractOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.abs.AbstractReadOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildIdCollection;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ReadForeignOwnerDto extends AbstractReadOwnerDto {

    @Builder
    public ReadForeignOwnerDto(String address, String city, String telephone, Set<String> hobbies, Set<Long> petIds) {
        super(address, city, telephone, hobbies, petIds);
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
