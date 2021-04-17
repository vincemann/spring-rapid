package com.github.vincemann.springrapid.coredemo.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentCollection;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Specialty extends IdentifiableEntityImpl<Long>
        implements BiDirChild {

    @Column(name = "description")
    private String description;


    @ManyToMany(mappedBy = "specialties")
    private Set<Vet> vets;


}
