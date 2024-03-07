package com.github.vincemann.springrapid.sync.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;

import java.util.Date;

/**
 * Set of object of this class is sent by client to server.
 * Server checks id's and compares its own {@link AuditingEntity#getLastModifiedById()} value with provided
 * client side timestamp.
 * Returns set of {@link EntitySyncStatus} for those id's that need syncing.
 * Client can decide what to do with this information.
 *
 */
public class LastFetchInfo {

    private String id;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date lastUpdate;

    // constructor for jpa
    public LastFetchInfo(Long id, Date lastUpdate) {
        this.id = String.valueOf(id);
        this.lastUpdate = lastUpdate;
    }

    public LastFetchInfo(String id, Date lastUpdate) {
        this.id = id;
        this.lastUpdate = lastUpdate;
    }

    public LastFetchInfo() {
    }

    public String getId() {
        return id;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public String toString() {
        return "LastFetchInfo{" +
                "id='" + id + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
