package com.github.vincemann.springrapid.coredemo.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentCollection;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Specialty extends IdentifiableEntityImpl<Long>
        implements BiDirChild {

    @Builder
    public Specialty(String description, Set<Vet> vets) {
        this.description = description;
        if (vets!=null)
            this.vets = vets;
    }

    @Unique
    @ToString.Include
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
