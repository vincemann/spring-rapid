package com.github.vincemann.springrapid.syncdemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.UniDirChildEntity;
import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentEntity;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import com.github.vincemann.springrapid.sync.model.entity.AuditingEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "pets", uniqueConstraints = @UniqueConstraint(name = "unique name", columnNames = "name"))
@Entity
public class Pet extends AuditingEntity<Long> {

    @NotEmpty
    @Column(name = "name", nullable = false, unique = true)
    private String name;


    //uniDir ManyToOne -> PetType does not know about this mapping
    @ManyToOne
    @JoinColumn(name = "pet_type_id")
    @UniDirChildEntity
    private PetType petType;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pet")
    private Set<Toy> toys = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    @BiDirParentEntity
    private Owner owner;


    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    public void removeToy(Toy toy){
        this.toys.remove(toy);
        toy.setPet(null);
    }



    @Builder
    public Pet(String name, PetType petType, Set<Toy> toys, Owner owner, LocalDate birthDate) {
        this.name = name;
        this.petType = petType;
        if (toys !=null)
            this.toys = toys;
        this.owner = owner;
        this.birthDate = birthDate;
    }

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
