package com.github.vincemann.springrapid.sync.softdelete;

import com.github.vincemann.springrapid.core.model.audit.IAuditingEntity;

import java.io.Serializable;
import java.util.Date;


public interface ISoftDeleteEntity<ID extends Serializable> extends IAuditingEntity<ID> {

    public Date getDeletedDate();
}
