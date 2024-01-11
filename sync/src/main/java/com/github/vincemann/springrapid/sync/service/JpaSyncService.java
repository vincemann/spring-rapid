package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.core.service.JPQLEntityFilter;
import com.github.vincemann.springrapid.sync.model.EntityLastUpdateInfo;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.sync.repo.CustomAuditingRepository;
import com.github.vincemann.springrapid.sync.repo.RapidCustomAuditingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class JpaSyncService
    // todo remove generic params not needed
        <
                E extends AuditingEntity<Id>,
                Id extends Serializable,
                R extends JpaRepository<E, Id>>
        implements SyncService<E, Id> {

    private IdConverter<Id> idConverter;
    // could not merge my custom repo with jpa repo for some reason, so custom repos are seperated
    // and everything that can be auto impl via jpaRepoInterface is subTypeRequirement for Repo generic type
    private CustomAuditingRepository<E,Id> auditingRepository;
    private AbstractCrudService<E,Id,R> crudService;

    @Lazy
    @Autowired
    public void injectCrudService(AbstractCrudService<E, Id,R> crudService) {
        this.crudService = crudService;
    }

    @Autowired
    public void initAuditingRepository(EntityManager entityManager) {
        this.auditingRepository= new RapidCustomAuditingRepository<>(entityManager,crudService.getEntityClass());
    }

    @Autowired
    public void setIdConverter(IdConverter<Id> idConverter) {
        this.idConverter = idConverter;
    }


    @Transactional
    @Override
    public EntitySyncStatus findEntitySyncStatus(EntityLastUpdateInfo lastUpdateInfo) {
        String id = lastUpdateInfo.getId();
        Id convertedId = idConverter.toId(id);
        Date lastModified = auditingRepository.findLastModifiedDateById(convertedId);
        SyncStatus status;
        boolean updated;
        if (lastModified == null) {
            assert crudService.findById(convertedId).isEmpty();
            updated = true;
            status = SyncStatus.REMOVED;
        } else {
            updated = lastUpdateInfo.getLastUpdate().before(lastModified);
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
        List<EntityLastUpdateInfo> updateInfosSince = auditingRepository.findLastUpdateInfosSince(lastClientFetch, jpqlFilters);
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
