package io.github.vincemann.springrapid.core.model;

import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
@NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifiableEntityImpl<?> other = (IdentifiableEntityImpl<?>) o;
        //added null check here, otherwise entities with null ids are considered equal
        //and cut down to one entity in a set for example
        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
