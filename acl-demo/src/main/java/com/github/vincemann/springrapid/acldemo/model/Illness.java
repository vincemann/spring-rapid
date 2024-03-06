package com.github.vincemann.springrapid.acldemo.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentCollection;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "illnesss", uniqueConstraints = @UniqueConstraint(name = "unique name", columnNames = "name"))
@Entity
public class Illness extends IdentifiableEntityImpl<Long> {

    @NotBlank
    @Size(min = 2, max = 30)
    @Column(name = "name", unique = true, nullable = false, length = 30)
    private String name;


    @ManyToMany(mappedBy = "illnesss", fetch = FetchType.EAGER)
    @BiDirParentCollection(Pet.class)
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
