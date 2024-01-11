package com.github.vincemann.springrapid.sync.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Set of object of this class is sent by client to server.
 * Server checks id's and compares its own {@link AuditingEntity#getLastModifiedById()} value with provided
 * client side timestamp.
 * Returns set of {@link EntitySyncStatus} for those id's that need syncing.
 * Client can decide what to do with this information.
 *
 * Can also be used as server side last update info!
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class EntityLastUpdateInfo {
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date lastUpdate;

    // constructor for jpa
    public EntityLastUpdateInfo(Long id, Date lastUpdate) {
        this.id = String.valueOf(id);
        this.lastUpdate = lastUpdate;
    }
}
