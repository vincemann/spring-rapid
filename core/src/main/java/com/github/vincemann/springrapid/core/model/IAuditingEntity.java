package com.github.vincemann.springrapid.core.model;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

public interface IAuditingEntity<ID extends Serializable> extends IdentifiableEntity<ID> {

    public ID getCreatedById();
    public Date getCreatedDate();

    public ID getLastModifiedById();

    public Date getLastModifiedDate();

    public void setLastModifiedDate(Date date);
}
