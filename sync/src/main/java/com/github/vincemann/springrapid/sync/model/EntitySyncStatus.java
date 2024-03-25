package com.github.vincemann.springrapid.sync.model;


/**
 * Response entity returned by {@link com.github.vincemann.springrapid.sync.controller.SyncEntityController} for client.
 * Encapsulates minimal information about sync status of specific entity.
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
