package com.github.vincemann.springrapid.acldemo;


import com.github.vincemann.springrapid.acldemo.pet.Pet;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "illnesss", uniqueConstraints = @UniqueConstraint(name = "unique name", columnNames = "name"))
@Entity
public class Illness extends MyEntity {

    @NotBlank
    @Size(min = 2, max = 30)
    @Column(name = "name", unique = true, nullable = false, length = 30)
    private String name;


    @ManyToMany(mappedBy = "illnesses", fetch = FetchType.EAGER)
    private Set<Pet> pets = new HashSet<>();

    @Builder
    public Illness(String name, Set<Pet> pets) {
        this.name = name;
        if (pets !=null)
            this.pets = pets;
    }

    @Override
    public String toString() {
        return "Illness{" +
                "id=" + (getId() == null ? "null" : getId().toString()) +
                ", name='" + name + '\'' +
                ", pets=" + LazyToStringUtil.toStringIfLoaded(pets,Pet::getName) +
                '}';
    }
}
