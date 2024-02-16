package com.github.vincemann.springrapid.acldemo.model;

import com.github.vincemann.springrapid.acldemo.model.abs.Person;
import com.github.vincemann.springrapid.acldemo.model.abs.UserAwareEntity;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.UniDirChildEntity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"last_name"})
})
public class Vet extends Person implements UserAwareEntity {

    @Builder
    public Vet(String firstName, String lastName, Set<Specialty> specialtys) {
        super(firstName, lastName);
        if(specialtys !=null)
            this.specialtys = specialtys;
    }

    @Override
    public String getAuthenticationName() {
        return getUser().getAuthenticationName();
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vet_specialtys",
            joinColumns = @JoinColumn(name = "vet_id"),
            inverseJoinColumns = @JoinColumn(name = "speciality_id"))
    @BiDirChildCollection(Specialty.class)
    private Set<Specialty> specialtys = new HashSet<>();

    @NotNull
    @UniDirChildEntity
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @Override
    public String toString() {
        return "Vet{" +
                super.toString() +
                "specialtys=" + Arrays.toString(specialtys.stream().map(Specialty::getDescription).toArray())  +
                '}';
    }
}
