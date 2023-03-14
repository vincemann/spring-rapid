package com.github.vincemann.springrapid.acldemo.model;

import com.github.vincemann.springrapid.acldemo.model.abs.MyIdentifiableEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
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
@ToString
public class PetType extends MyIdentifiableEntity<Long> {
    @Unique
    private String name;
}
