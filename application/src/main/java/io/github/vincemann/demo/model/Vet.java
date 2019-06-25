package io.github.vincemann.demo.model;

import io.github.vincemann.demo.model.abs.Person;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vets")
public class Vet extends Person {

    @Builder
    public Vet(String firstName, String lastName, Set<Specialty> specialties) {
        super(firstName, lastName);
        if(specialties!=null) {
            this.specialties = specialties;
        }else {
            this.specialties= new HashSet<>();
        }
    }

    //unilateral
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vet_specialties",
            joinColumns = @JoinColumn(name = "vet_id"),
            inverseJoinColumns = @JoinColumn(name = "speciality_id"))
    private Set<Specialty> specialties = new HashSet<>();
}
