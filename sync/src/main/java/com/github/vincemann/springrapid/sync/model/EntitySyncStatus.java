package com.github.vincemann.springrapid.sync.model;


/**
 * Sync status response is Set of objects of this class. i.E.: [42a,3u,10u,4r]
 * id concatenated with status char a=added, u=updated, r=removed
 */

public class EntitySyncStatus {


    private String id;
    private SyncStatus status;


    public EntitySyncStatus(String id, SyncStatus status) {
        this.id = id;
        this.status = status;
    }

    public EntitySyncStatus(String id, Character status) {
        this.id = id;
        this.status = SyncStatus.convert(status);
    }

    public EntitySyncStatus() {
    }

    public String getId() {
        return id;
    }

    public SyncStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "EntitySyncStatus{" +
                "id='" + id + '\'' +
                ", status=" + status.name() +
                '}';
    }
}
