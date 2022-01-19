package com.github.vincemann.springrapid.coredemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import java.util.Arrays;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "illnesss")
@Entity
public class Illness extends IdentifiableEntityImpl<Long>  {
    @Unique
    private String name;


    @ManyToOne(fetch = FetchType.EAGER)
    @BiDirParentEntity
    @JsonBackReference
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Builder
    public Illness(@Unique String name, Pet pet) {
        this.name = name;
        this.pet = pet;
    }

    @Override
    public String toString() {
        return "Illness{" +
                "name='" + name + '\'' +
                ", pets=" +  pet == null ? "null" : pet.getName() +
                '}';
    }
}
