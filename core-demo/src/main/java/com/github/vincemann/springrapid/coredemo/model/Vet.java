package com.github.vincemann.springrapid.coredemo.model;

import com.github.vincemann.springrapid.coredemo.model.abs.Person;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.UniDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vets")
@ToString(callSuper = true)
public class Vet extends Person implements BiDirParent {

    @Builder
    public Vet(String firstName, String lastName, Set<Specialty> specialties) {
        super(firstName, lastName);
        if(specialties!=null) {
            this.specialties = specialties;
        }else {
            this.specialties= new HashSet<>();
        }
    }


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vet_specialties",
            joinColumns = @JoinColumn(name = "vet_id"),
            inverseJoinColumns = @JoinColumn(name = "speciality_id"))
    @BiDirChildCollection(Specialty.class)
    private Set<Specialty> specialties = new HashSet<>();
}
