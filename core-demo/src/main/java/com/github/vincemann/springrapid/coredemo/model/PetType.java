package com.github.vincemann.springrapid.coredemo.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import com.github.vincemann.springrapid.coredemo.model.abs.MyIdentifiableEntity;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.Entity;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pet_types")
@Builder
public class PetType extends MyIdentifiableEntity<Long> {
    @Unique
    private String name;
}
