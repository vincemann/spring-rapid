package io.github.vincemann.springrapid.demo.model;

import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

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
