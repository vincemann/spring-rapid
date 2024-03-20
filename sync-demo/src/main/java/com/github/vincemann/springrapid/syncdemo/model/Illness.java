package com.github.vincemann.springrapid.syncdemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.IdAwareEntityImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "illnesss", uniqueConstraints = @UniqueConstraint(name = "unique name", columnNames = "name"))
@Entity
public class Illness extends IdAwareEntityImpl<Long> {


    @NotBlank
    @Size(min = 2, max = 30)
    @Column(name = "name", unique = true, nullable = false, length = 30)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @BiDirParentEntity
    @JsonBackReference
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Builder
    public Illness(String name, Pet pet) {
        this.name = name;
        this.pet = pet;
    }

    @Override
    public String toString() {
        return "Illness{" +
                "id=" + (getId() == null ? "null" : getId().toString()) +
                ", name='" + name + '\'' +
                ", pets=" +  pet == null ? "null" : pet.getName() +
                '}';
    }
}
