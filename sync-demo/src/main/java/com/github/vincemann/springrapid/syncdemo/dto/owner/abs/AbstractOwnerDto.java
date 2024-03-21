package com.github.vincemann.springrapid.syncdemo.dto.owner.abs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractOwnerDto {

    private String address;
    protected String city;
    private String telephone;
    private List<String> hobbies = new ArrayList<>();

    public AbstractOwnerDto(String address, String city, String telephone, List<String> hobbies) {
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        if (hobbies!=null)
            this.hobbies=hobbies;
    }




}
