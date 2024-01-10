package com.github.vincemann.springrapid.sync.dto;

import lombok.*;

/**
 * Sync status response is Set of objects of this class. i.E.: [42a,3u,10u,4r]
 * id concatenated with status char a=added, u=updated, r=removed
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EntitySyncStatus {
    private String id;
    private Character status;

    @Builder
    public EntitySyncStatus(String id, Character status) {
        this.id = id;
        this.status = status;
    }
}
