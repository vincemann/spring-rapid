package io.github.vincemann.springrapid.demo.model;

import io.github.vincemann.springrapid.demo.model.abs.Person;
import io.github.vincemann.springrapid.entityrelationship.model.uniDir.child.UniDirChildCollection;
import io.github.vincemann.springrapid.entityrelationship.model.uniDir.parent.UniDirParent;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vets")
@ToString
public class Vet extends Person implements UniDirParent {

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
    @UniDirChildCollection(Specialty.class)
    private Set<Specialty> specialties = new HashSet<>();
}
