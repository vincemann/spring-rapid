package io.github.vincemann.demo.model;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Specialty extends IdentifiableEntityImpl<Long> {

    @Column(name = "description")
    private String description;
}
