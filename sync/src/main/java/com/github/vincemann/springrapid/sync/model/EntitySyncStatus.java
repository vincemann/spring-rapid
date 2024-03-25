package com.github.vincemann.springrapid.sync.model;


/**
 * Response body returned by {@link com.github.vincemann.springrapid.sync.controller.SyncEntityController controllers} endpoints for client.
 * Encapsulates minimal information about sync status of specific entity.
 * Entity can either be marked as {@link SyncStatus#UPDATED} or {@link SyncStatus#REMOVED}.
 *
 * @see com.github.vincemann.springrapid.sync.controller.SyncEntityController
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
