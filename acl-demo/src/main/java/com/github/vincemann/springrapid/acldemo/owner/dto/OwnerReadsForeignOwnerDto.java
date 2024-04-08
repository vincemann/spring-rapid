package com.github.vincemann.springrapid.acldemo.owner.dto;

import com.github.vincemann.springrapid.acldemo.owner.dto.abs.AbstractReadOwnerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OwnerReadsForeignOwnerDto extends AbstractReadOwnerDto {
    @Override
    public String toString() {
        return "OwnerReadsForeignOwnerDto{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", city='" + getCity() + '\'' +
                ", telephone='" + getTelephone() + '\'' +
                ", hobbies=" + getHobbies() +
                '}';
    }
}
