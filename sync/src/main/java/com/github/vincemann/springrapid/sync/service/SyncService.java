package com.github.vincemann.springrapid.sync.service;


import com.github.vincemann.springrapid.sync.model.entity.IAuditingEntity;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.LastFetchInfo;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * Service for fetching {@link EntitySyncStatus} of entity or multiple entities for specific entity type.
 * @param <E> type of entity, must implement {@link IAuditingEntity}
 * @param <Id> id type of entity
 *
 * @see com.github.vincemann.springrapid.sync.controller.SyncEntityController
 * @see DefaultSyncService
 * @see com.github.vincemann.springrapid.sync.softdelete.SoftDeleteSyncService
 */
public interface SyncService<E extends IAuditingEntity<Id>,Id extends Serializable> {


    /**
     * Method for finding {@link EntitySyncStatus} of specific entity.
     *
     * @param lastClientUpdate info about when client performed last fetch|update for specific entity
     * @return sync status of entity (updated or removed) or null if no updated required
     * @throws EntityNotFoundException if entity with {@link LastFetchInfo#getId()} not found.
     */
    @Nullable
    EntitySyncStatus findEntitySyncStatus(LastFetchInfo lastClientUpdate) throws EntityNotFoundException;

    /**
     * Finds sync statuses for all entities updated since {@code lastClientUpdate} timestamp.
     * Note that {@link com.github.vincemann.springrapid.sync.model.SyncStatus#REMOVED} can only be evaluated when using {@link com.github.vincemann.springrapid.sync.softdelete.SoftDeleteSyncService} impl.
     * If you dont use soft delete but still want to also find removed status, use {@link this#findEntitySyncStatuses(Collection)} instead.
     *
     * @param lastClientUpdate timestamp client performed last update of all entities
     * @return list of {@link EntitySyncStatus sync statuses} <strong>only</strong> for updated entities (and removed entities when using soft delete impl)
     *         Entities that dont need update are not present in result list.
     */
    List<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastClientUpdate);

    /**
     * Find sync statuses for multiple entities.
     * Need {@link LastFetchInfo} for each entity.
     * Fetching entities can be done with {@link org.springframework.data.jpa.repository.JpaRepository#findAllById(Iterable)}.
     *
     * @param lastClientUpdates
     * @return List of {@link EntitySyncStatus sync statuses} for updated and removed entities, even in {@link DefaultSyncService} impl.
     * @throws EntityNotFoundException if any entity within given collection of {@link LastFetchInfo#getId()} not found.
     */
    List<EntitySyncStatus> findEntitySyncStatuses(Collection<LastFetchInfo> lastClientUpdates) throws EntityNotFoundException;
}
