package com.github.vincemann.springrapid.acldemo;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pet_types", uniqueConstraints = @UniqueConstraint(name = "unique name", columnNames = "name"))
@Builder
public class PetType extends MyEntity {

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
