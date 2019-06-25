package io.github.vincemann.demo.model;

import lombok.*;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;

import javax.persistence.Entity;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pet_types")
@Builder
public class PetType extends IdentifiableEntityImpl<Long> {
    private String name;
}
