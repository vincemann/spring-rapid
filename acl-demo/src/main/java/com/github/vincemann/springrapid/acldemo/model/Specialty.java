package com.github.vincemann.springrapid.acldemo.model;

import com.github.vincemann.springrapid.core.model.IdAwareImpl;


import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import lombok.*;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "specialties", uniqueConstraints = @UniqueConstraint(name = "unique description", columnNames = "description"))
@Entity
public class Specialty extends IdAwareImpl<Long>
{

    @NotEmpty
    @Column(name = "description", nullable = false, unique = true)
    private String description;


    @ManyToMany(mappedBy = "specialtys", fetch = FetchType.EAGER)
    private Set<Vet> vets = new HashSet<>();

    @Builder
    public Specialty(String description, Set<Vet> vets) {
        this.description = description;
        if (vets!=null)
            this.vets = vets;
    }



    @Override
    public String toString() {
        return "Specialty{" +
                "id=" + (getId() == null ? "null" : getId().toString()) +
                ", description='" + description + '\'' +
                ", vets=" + LazyToStringUtil.toStringIfLoaded(vets,Vet::getLastName) +
                '}';
    }
}
