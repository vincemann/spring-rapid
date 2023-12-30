package com.github.vincemann.springrapid.coredemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import com.github.vincemann.springrapid.coredemo.model.abs.MyIdentifiableEntity;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "toys")
@Entity
@Builder
public class Toy extends MyIdentifiableEntity<Long> {
    @Unique
    private String name;

    @ManyToOne
    @BiDirParentEntity
    @JsonBackReference
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Override
    public String toString() {
        return "test";
//        return "Toy{" +
//                "name='" + name + '\'' +
//                ", pet=" + LazyToStringUtil.toStringIfLoaded(pet,Pet::getName) +
//                '}';
    }
}
