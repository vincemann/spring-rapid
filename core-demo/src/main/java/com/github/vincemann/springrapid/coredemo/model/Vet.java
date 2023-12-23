package com.github.vincemann.springrapid.coredemo.model;

import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import com.github.vincemann.springrapid.coredemo.model.abs.Person;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;

import lombok.*;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vets")
public class Vet extends Person
{

    @Builder
    public Vet(String firstName, String lastName, Set<Specialty> specialtys) {
        super(firstName, lastName);
        if(specialtys !=null)
            this.specialtys = specialtys;
    }


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vet_specialtys",
            joinColumns = @JoinColumn(name = "vet_id"),
            inverseJoinColumns = @JoinColumn(name = "speciality_id"))
    @BiDirChildCollection(Specialty.class)
    private Set<Specialty> specialtys = new HashSet<>();

    @Override
    public String toString() {
        return "Vet{" +
                "firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", specialtys=" + LazyToStringUtil.toStringIfLoaded(specialtys,Specialty::getDescription) +
                '}';
    }


}
