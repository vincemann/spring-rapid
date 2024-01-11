package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.JPQLEntityFilter;
import com.github.vincemann.springrapid.sync.model.EntityLastUpdateInfo;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.sync.repo.AuditingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class SyncJpaCrudService
        <
                E extends AuditingEntity<Id>,
                Id extends Serializable,
                R extends RapidJpaRepository<E, Id> & AuditingRepository<E, Id>>
        extends JPACrudService<E, Id, R>
        implements SyncService<E, Id> {

    private IdConverter<Id> idConverter;

    @Autowired
    public void setIdConverter(IdConverter<Id> idConverter) {
        this.idConverter = idConverter;
    }


    @Transactional
    @Override
    public EntitySyncStatus findEntitySyncStatus(EntityLastUpdateInfo lastUpdateInfo) {
        String id = lastUpdateInfo.getId();
        Id convertedId = idConverter.toId(id);
        Date lastModified = getRepository().findLastModifiedDateByIdEquals(convertedId);
        SyncStatus status;
        boolean updated;
        if (lastModified == null) {
            assert getRepository().findById(convertedId).isEmpty();
            updated = true;
            status = SyncStatus.REMOVED;
        } else {
            updated = lastUpdateInfo.getLastUpdate().after(lastModified);
            status = SyncStatus.UPDATED;
        }
        if (updated) {
            return EntitySyncStatus.builder()
                    .id(id)
                    .status(status)
                    .build();
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public Set<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastClientFetch, List<JPQLEntityFilter<E>> jpqlFilters) {
        // server side update info
        Set<EntitySyncStatus> result = new HashSet<>();
        // todo overwrite and check soft delete timestamp
        List<EntityLastUpdateInfo> updateInfosSince = getRepository().findLastUpdateInfosSince(lastClientFetch, jpqlFilters);
        for (EntityLastUpdateInfo lastUpdateInfo : updateInfosSince) {
            if (lastUpdateInfo.getLastUpdate().after(lastClientFetch)) {
                result.add(
                        EntitySyncStatus.builder()
                                .id(lastUpdateInfo.getId())
                                .status(SyncStatus.UPDATED)
                                .build());
            }
        }
        return result;
    }

    /**
     * only returns set of {@link EntitySyncStatus} for entities that need update.
     */
    @Transactional
    @Override
    public Set<EntitySyncStatus> findEntitySyncStatuses(Collection<EntityLastUpdateInfo> lastUpdateInfos) {
        // todo speed this up maybe
        // maybe add parallel flag ?
        return lastUpdateInfos.stream()
                .map(this::findEntitySyncStatus)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
