package com.github.vincemann.springrapid.acldemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.UniDirChildEntity;


import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class Pet extends IdentifiableEntityImpl<Long> {

    @NotEmpty
    @Column(name = "name", nullable = false, unique = true)
    private String name;


    //uniDir ManyToOne -> PetType does not know about this mapping
    @ManyToOne
    @JoinColumn(name = "pet_type_id", nullable = false)
    @UniDirChildEntity
    private PetType petType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "pet_illnesss",
            joinColumns = @JoinColumn(name = "pet_id"),
            inverseJoinColumns = @JoinColumn(name = "illness_id"))
    @BiDirChildCollection(Illness.class)
    private Set<Illness> illnesss = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    @BiDirParentEntity
    private Owner owner;

    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Builder
    public Pet( String name, PetType petType, Set<Illness> illnesss, Owner owner, LocalDate birthDate) {
        this.name = name;
        this.petType = petType;
        if (illnesss !=null)
            this.illnesss = illnesss;
        this.owner = owner;
        this.birthDate = birthDate;
    }


    @Override
    public String toString() {
        return "Pet{" +
                "name='" + name + '\'' +
                ", petType=" + petType +
                ", illnesses=" +  (illnesss == null ? "null" : Arrays.toString(illnesss.stream().map(Illness::getName).toArray())) +
                ", owner=" + (owner==null? "null": owner.getLastName()) +
                ", birthDate=" + birthDate +
                '}';
    }
}
