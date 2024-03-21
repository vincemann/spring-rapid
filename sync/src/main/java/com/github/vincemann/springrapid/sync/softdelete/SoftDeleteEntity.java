package com.github.vincemann.springrapid.sync.softdelete;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.vincemann.springrapid.sync.model.entity.AuditingEntity;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@JsonIgnoreProperties({ "createdById", "lastModifiedById", "createdDate", "lastModifiedDate", "new","deletedDate" })
public class SoftDeleteEntity<ID extends Serializable>
        extends AuditingEntity<ID>
            implements ISoftDeleteEntity<ID> {

    public static final String DELETED_FIELD = "deletedDate";

//    private ID deletedById;


    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;

    public SoftDeleteEntity() {
    }

    @Override
    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }
}
