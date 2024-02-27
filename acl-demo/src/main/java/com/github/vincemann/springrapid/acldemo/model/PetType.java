package com.github.vincemann.springrapid.acldemo.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pet_types", uniqueConstraints = @UniqueConstraint(name = "unique name", columnNames = "name"))
@Builder
public class PetType extends IdentifiableEntityImpl<Long> {

    @NotEmpty
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Override
    public String toString() {
        return "PetType{" +
                "id=" + (getId() == null ? "null" : getId().toString()) +
                ", name='" + name + '\'' +
                ", id=" + getId() +
                '}';
    }
}
