package com.github.vincemann.springrapid.coredemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.UniDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "pets")
@Entity
public class Pet extends IdentifiableEntityImpl<Long> implements BiDirChild, UniDirParent, BiDirParent {


    @Builder
    public Pet(@Unique String name, PetType petType, Set<Toy> toys, Owner owner, LocalDate birthDate) {
        this.name = name;
        this.petType = petType;
        if (toys !=null)
            this.toys = toys;
        this.owner = owner;
        this.birthDate = birthDate;
    }

    @Column(name = "name")
    @Unique
    private String name;


    //uniDir ManyToOne -> PetType does not know about this mapping
    @ManyToOne
    @JoinColumn(name = "pet_type_id")
    @UniDirChildEntity
    private PetType petType;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pet")
    @BiDirChildCollection(Toy.class)
    private Set<Toy> toys = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    @BiDirParentEntity
    private Owner owner;


    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Override
    public String toString() {
        return "Pet{" +
                "name='" + name + '\'' +
                ", petType=" + petType +
                ", illnesses=" +  Arrays.toString(toys.stream().map(Toy::getName).toArray()) +
                ", owner=" + (owner==null? "null": owner.getLastName()) +
                ", birthDate=" + birthDate +
                '}';
    }
}
