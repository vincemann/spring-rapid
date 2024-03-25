package com.github.vincemann.springrapid.sync.softdelete;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.vincemann.springrapid.sync.model.EntityUpdateInfo;

import java.util.Date;


/**
 * Extended version of {@link EntityUpdateInfo} providing also deleted timestamp.
 */
public class SoftDeleteEntityUpdateInfo extends EntityUpdateInfo {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date deletedDate;

    public SoftDeleteEntityUpdateInfo(Long id, Date lastUpdate, Date deletedDate) {
        super(id, lastUpdate);
        this.deletedDate = deletedDate;
    }

    public SoftDeleteEntityUpdateInfo() {
    }

    @Override
    public String toString() {
        return "SoftDeleteEntityUpdateInfo{" +
                "deletedDate=" + deletedDate +
                ", id='" + getId() + '\'' +
                ", lastUpdate=" + getLastUpdate() +
                '}';
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }
}
