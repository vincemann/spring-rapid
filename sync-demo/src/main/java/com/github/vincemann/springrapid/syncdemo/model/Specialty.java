package com.github.vincemann.springrapid.syncdemo.model;

import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentCollection;
import com.github.vincemann.springrapid.core.model.IdAwareEntityImpl;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "specialties", uniqueConstraints = @UniqueConstraint(name = "unique description", columnNames = "description"))
@Entity
public class Specialty extends IdAwareEntityImpl<Long>
{

    @Builder
    public Specialty(String description, Set<Vet> vets) {
        this.description = description;
        if (vets!=null)
            this.vets = vets;
    }

    @NotEmpty
    @Column(name = "description", nullable = false, unique = true)
    private String description;


    @ManyToMany(mappedBy = "specialtys", fetch = FetchType.EAGER)
    @BiDirParentCollection(Vet.class)
    private Set<Vet> vets = new HashSet<>();

    @Override
    public String toString() {
        return "Specialty{" +
                "id=" + (getId() == null ? "null" : getId().toString()) +
                ", description='" + description + '\'' +
                ", vets=" + LazyToStringUtil.toStringIfLoaded(vets,Vet::getLastName) +
                '}';
    }
}
