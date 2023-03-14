package com.github.vincemann.springrapid.acldemo.model;

import com.github.vincemann.springrapid.acldemo.model.abs.MyIdentifiableEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentCollection;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Specialty extends MyIdentifiableEntity<Long>
         {

    @Builder
    public Specialty(String description, Set<Vet> vets) {
        this.description = description;
        if (vets!=null)
            this.vets = vets;
    }

    @Unique
    @Column(name = "description")
    private String description;


    @ManyToMany(mappedBy = "specialtys", fetch = FetchType.EAGER)
    @BiDirParentCollection(Vet.class)
    private Set<Vet> vets = new HashSet<>();

    @Override
    public String toString() {
        return "Specialty{" +
                "description='" + description + '\'' +
                ", vets=" + Arrays.toString(vets.stream().map(Vet::getLastName).toArray())  +
                '}';
    }
}
