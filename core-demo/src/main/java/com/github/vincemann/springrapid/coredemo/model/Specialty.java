package com.github.vincemann.springrapid.coredemo.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
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
