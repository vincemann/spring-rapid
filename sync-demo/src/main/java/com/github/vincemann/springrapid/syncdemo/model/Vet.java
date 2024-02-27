package com.github.vincemann.springrapid.syncdemo.model;

import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildCollection;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import com.github.vincemann.springrapid.syncdemo.model.abs.Person;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vets", uniqueConstraints = @UniqueConstraint(name = "unique last name", columnNames = "lastName"))
public class Vet extends Person
{
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vet_specialtys",
            joinColumns = @JoinColumn(name = "vet_id"),
            inverseJoinColumns = @JoinColumn(name = "speciality_id"))
    @BiDirChildCollection(Specialty.class)
    private Set<Specialty> specialtys = new HashSet<>();

    @Builder
    public Vet(String firstName, String lastName, Set<Specialty> specialtys) {
        super(firstName, lastName);
        if(specialtys !=null)
            this.specialtys = specialtys;
    }


    @Override
    public String toString() {
        return "Vet{" +
                "id=" + (getId() == null ? "null" : getId().toString()) +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", specialtys=" + LazyToStringUtil.toStringIfLoaded(specialtys,Specialty::getDescription) +
                '}';
    }


}
