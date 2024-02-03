package com.github.vincemann.springrapid.syncdemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
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
public class Toy extends IdentifiableEntityImpl<Long> {
    @Unique
    private String name;

    @ManyToOne
    @BiDirParentEntity
    @JsonBackReference
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Override
    public String toString() {
        return "Toy{" +
                "id=" + (getId() == null ? "null" : getId().toString()) +
                ", name='" + name + '\'' +
                ", pet=" + LazyToStringUtil.toStringIfLoaded(pet,Pet::getName) +
                '}';
    }
}
