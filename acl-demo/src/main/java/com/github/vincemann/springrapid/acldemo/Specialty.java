package com.github.vincemann.springrapid.acldemo;

import com.github.vincemann.springrapid.acldemo.vet.Vet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "specialties", uniqueConstraints = @UniqueConstraint(name = "unique description", columnNames = "description"))
@Entity
public class Specialty extends MyEntity
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
                ", vets=" + vets.stream().map(Vet::getLastName).collect(Collectors.toList()) +
                '}';
    }
}
