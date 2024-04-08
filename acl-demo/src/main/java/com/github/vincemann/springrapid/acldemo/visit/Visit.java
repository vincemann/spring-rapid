package com.github.vincemann.springrapid.acldemo.visit;


import com.github.vincemann.springrapid.acldemo.MyEntity;
import com.github.vincemann.springrapid.acldemo.owner.Owner;
import com.github.vincemann.springrapid.acldemo.pet.Pet;
import com.github.vincemann.springrapid.acldemo.vet.Vet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "visits")
@Entity
public class Visit extends MyEntity {

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id")
    private Set<Pet> pets = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vet_id")
    private Vet vet;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "reason")
    private String reason;

    public void addPet(Pet pet){
        this.pets.add(pet);
    }

    @Builder
    public Visit(Set<Pet> pets, Owner owner, Vet vet, LocalDate date, String reason) {
        if(pets!=null)
            this.pets = pets;
        this.owner = owner;
        this.vet = vet;
        this.date = date;
        this.reason = reason;
    }


    @Override
    public String toString() {
        return "Visit{" +
                "id=" + (getId() == null ? "null" : getId().toString()) +
                ", pets=" + pets.stream().map(Pet::getName).collect(Collectors.toList()) +
                ", owner=" + owner == null ? "null" : owner.getLastName() +
                ", vet=" + vet == null ? "null" : vet.getLastName() +
                ", date=" + date +
                ", reason='" + reason + '\'' +
                '}';
    }
}
