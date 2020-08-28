package com.github.vincemann.springrapid.core.model;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public interface IdentifiableEntity<Id extends Serializable> extends Serializable {
    public Id getId();
    public void setId(Id id);
}
