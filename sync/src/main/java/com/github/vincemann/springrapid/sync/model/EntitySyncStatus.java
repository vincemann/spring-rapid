package com.github.vincemann.springrapid.sync.model;

import lombok.*;

/**
 * Sync status response is Set of objects of this class. i.E.: [42a,3u,10u,4r]
 * id concatenated with status char a=added, u=updated, r=removed
 */
@Getter
@Setter
@NoArgsConstructor
public class EntitySyncStatus {


    private String id;
    private SyncStatus status;


    @Builder
    public EntitySyncStatus(String id, SyncStatus status) {
        this.id = id;
        this.status = status;
    }

    public EntitySyncStatus(String id, Character status) {
        this.id = id;
        this.status = SyncStatus.convert(status);
    }

    @Override
    public String toString() {
        return "EntitySyncStatus{" +
                "id='" + id + '\'' +
                ", status=" + status.name() +
                '}';
    }
}
