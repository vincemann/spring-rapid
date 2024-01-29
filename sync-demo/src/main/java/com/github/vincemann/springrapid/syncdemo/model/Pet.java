package com.github.vincemann.springrapid.syncdemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.UniDirChildEntity;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "pets")
@Entity
public class Pet extends AuditingEntity<Long> {


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
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pet")
    @BiDirChildCollection(Toy.class)
    private Set<Toy> toys = new HashSet<>();

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pet")
    @BiDirChildCollection(Illness.class)
    private Set<Illness> illnesss = new HashSet<>();


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
                "id=" + (getId() == null ? "null" : getId().toString()) +
                ", name='" + name + '\'' +
                ", petType=" + petType +
                ", toys=" +  LazyToStringUtil.toStringIfLoaded(toys,Toy::getName) +
                ", owner=" + LazyToStringUtil.toStringIfLoaded(owner,Owner::getLastName) +
                ", birthDate=" + birthDate +
                '}';
    }
}
