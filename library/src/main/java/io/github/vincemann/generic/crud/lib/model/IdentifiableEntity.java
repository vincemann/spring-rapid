package io.github.vincemann.generic.crud.lib.model;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public interface IdentifiableEntity<Id extends Serializable & Comparable> extends Serializable {

    public Id getId();

    public void setId(Id id);
}
