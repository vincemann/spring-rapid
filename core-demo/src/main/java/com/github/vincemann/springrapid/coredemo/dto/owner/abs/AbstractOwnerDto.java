package com.github.vincemann.springrapid.coredemo.dto.owner.abs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
public abstract class AbstractOwnerDto {

    private String address;
    protected String city;
    private String telephone;
    private Set<String> hobbies = new HashSet<>();

    public AbstractOwnerDto(String address, String city, String telephone,Set<String> hobbies) {
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        if (hobbies!=null)
            this.hobbies=hobbies;
    }




}
