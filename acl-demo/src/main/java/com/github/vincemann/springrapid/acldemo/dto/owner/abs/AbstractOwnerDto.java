package com.github.vincemann.springrapid.acldemo.dto.owner.abs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractOwnerDto{

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String telephone;

    private Set<String> hobbies = new HashSet<>();

    public AbstractOwnerDto(String firstName, String lastName, String address, String city, String telephone, Set<String> hobbies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        if (hobbies != null)
            this.hobbies = hobbies;
    }
}
