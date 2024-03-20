package com.github.vincemann.springrapid.sync.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


/**
 * Encapsulates minimal update information for entity with given {@link #id}.
 */
public class EntityUpdateInfo {
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date lastUpdate;

    // constructor for jpa
    public EntityUpdateInfo(Long id, Date lastUpdate) {
        this.id = String.valueOf(id);
        this.lastUpdate = lastUpdate;
    }

    public EntityUpdateInfo(String id, Date lastUpdate) {
        this.id = id;
        this.lastUpdate = lastUpdate;
    }

    public EntityUpdateInfo() {
    }

    public String getId() {
        return id;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public String toString() {
        return "EntityUpdateInfo{" +
                "id='" + id + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
