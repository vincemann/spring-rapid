package com.github.vincemann.springrapid.coredemo.model;

import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.UniDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.UniDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Visit extends IdentifiableEntityImpl<Long> implements UniDirParent {


    @OneToMany
    @JoinColumn(name = "pet_id")
    @UniDirChildCollection(Pet.class)
    private Set<Pet> pets = new HashSet<>();

    @OneToOne
    @UniDirChildEntity
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToOne
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
                "pets=" + Arrays.toString(pets.stream().map(Pet::getName).toArray())  +
                ", owner=" + (owner==null? "null": owner.getLastName()) +
                ", vet=" + (vet==null? "null": vet.getLastName()) +
                ", date=" + date +
                ", reason='" + reason + '\'' +
                '}';
    }
}
