package com.github.vincemann.springrapid.acldemo.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentCollection;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "illnesss")
@Entity
public class Illness extends IdentifiableEntityImpl<Long> implements BiDirChild {
    @Unique
    private String name;

    @ManyToMany(mappedBy = "illnesss", fetch = FetchType.EAGER)
    @BiDirParentCollection(Vet.class)
    private Set<Pet> pets = new HashSet<>();

    @Builder
    public Illness(@Unique String name, Set<Pet> pets) {
        this.name = name;
        if (pets !=null)
            this.pets = pets;
    }

    @Override
    public String toString() {
        return "Toy{" +
                "name='" + name + '\'' +
                ", pets=" +  Arrays.toString(pets.stream().map(Pet::getName).toArray()) +
                '}';
    }
}
