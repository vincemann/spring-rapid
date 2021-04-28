package com.github.vincemann.springrapid.acldemo.model;

import com.github.vincemann.springrapid.acldemo.model.abs.Person;
import com.github.vincemann.springrapid.acldemo.model.abs.UserAware;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.UniDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vets")
public class Vet extends Person implements BiDirParent, UserAware, UniDirParent {

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

    @UniDirChildEntity
    @OneToOne(cascade = CascadeType.REMOVE)
    private User user;

    @Override
    public String toString() {
        return "Vet{" +
                super.toString() +
                "specialtys=" + Arrays.toString(specialtys.stream().map(Specialty::getDescription).toArray())  +
                '}';
    }
}
