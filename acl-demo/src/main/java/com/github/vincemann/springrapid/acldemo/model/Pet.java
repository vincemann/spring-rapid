package com.github.vincemann.springrapid.acldemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.core.model.IdAwareImpl;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "pets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
@Entity
public class Pet extends IdAwareImpl<Long> {

    @NotEmpty
    @Column(name = "name", nullable = false, unique = true)
    private String name;


    //uniDir ManyToOne -> PetType does not know about this mapping
    @ManyToOne
    @JoinColumn(name = "pet_type_id", nullable = false)
    private PetType petType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "pet_illnesss",
            joinColumns = @JoinColumn(name = "pet_id"),
            inverseJoinColumns = @JoinColumn(name = "illness_id"))
    private Set<Illness> illnesses = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    private Owner owner;

    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    public void addIllness(Illness illness) {
        this.illnesses.add(illness);
        illness.getPets().add(this);
    }

    public void removeIllness(Illness illness){
        this.illnesses.remove(illness);
        illness.getPets().remove(this);
    }

    @Builder
    public Pet(String name, PetType petType, Set<Illness> illnesses, Owner owner, LocalDate birthDate) {
        this.name = name;
        this.petType = petType;
        if (illnesses !=null)
            this.illnesses = illnesses;
        this.owner = owner;
        this.birthDate = birthDate;
    }


    @Override
    public String toString() {
        return "Pet{" +
                "name='" + name + '\'' +
                ", petType=" + petType +
                ", illnesses=" +  (illnesses == null ? "null" : Arrays.toString(illnesses.stream().map(Illness::getName).toArray())) +
                ", owner=" + (owner==null? "null": owner.getLastName()) +
                ", birthDate=" + birthDate +
                '}';
    }

}
