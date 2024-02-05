package com.github.vincemann.springrapid.core.model.audit;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.io.Serializable;
import java.util.Date;

public interface IAuditingEntity<Id extends Serializable> extends IdentifiableEntity<Id> {

    public Id getCreatedById();
    public Date getCreatedDate();

    public Id getLastModifiedById();

    public Date getLastModifiedDate();

    public void setLastModifiedDate(Date date);

    public void setLastModifiedById(Id id);
}
