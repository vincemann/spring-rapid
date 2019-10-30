package io.github.vincemann.generic.crud.lib.model;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class IdentifiableEntityImpl<Id extends Serializable> implements IdentifiableEntity<Id> {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @javax.persistence.Id
    private Id id;

    @Override
    public Id getId() {
        return this.id;
    }

    @Override
    public void setId(Id id) {
        this.id=id;
    }
}
