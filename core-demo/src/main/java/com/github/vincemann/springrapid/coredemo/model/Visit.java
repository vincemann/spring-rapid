package com.github.vincemann.springrapid.coredemo.model;

import com.github.vincemann.springrapid.autobidir.model.child.annotation.UniDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.UniDirChildEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "visits")
public class Visit extends IdentifiableEntityImpl<Long> {


    @Builder
    public Visit(Set<Pet> pets, Owner owner, Vet vet, LocalDate date, String reason) {
        if(pets!=null)
            this.pets = pets;
        this.owner = owner;
        this.vet = vet;
        this.date = date;
        this.reason = reason;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id")
    @UniDirChildCollection(Pet.class)
    private Set<Pet> pets = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    @UniDirChildEntity
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToOne(fetch = FetchType.EAGER)
    @UniDirChildEntity
    @JoinColumn(name = "vet_id")
    private Vet vet;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "reason")
    private String reason;

    @Override
    public String toString() {
        return "Visit{" +
                "pets=" + LazyToStringUtil.toStringIfLoaded(pets,Pet::getName) +
                ", owner=" + LazyToStringUtil.toStringIfLoaded(owner,Owner::getLastName) +
                ", vet=" + LazyToStringUtil.toStringIfLoaded(vet,Vet::getLastName) +
                ", date=" + date +
                ", reason='" + reason + '\'' +
                '}';
    }
}
