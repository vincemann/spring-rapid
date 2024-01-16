package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.util.FilterUtils;
import com.github.vincemann.springrapid.sync.model.EntityLastUpdateInfo;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.sync.repo.AuditingRepository;
import com.github.vincemann.springrapid.sync.repo.RapidAuditingRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
// todo could create JpaSyncCrudService - but I prefer composition over inheritance
// maybe remove id param?

public class JpaSyncService<E extends AuditingEntity<Id>, Id extends Serializable>
        implements SyncService<E, Id> , InitializingBean {

    private IdConverter<Id> idConverter;
    // could not merge my custom repo with jpa repo for some reason, so custom repos are seperated
    // and everything that can be auto impl via jpaRepoInterface is subTypeRequirement for Repo generic type
    private AuditingRepository<E,Id> auditingRepository;
    private AbstractCrudService<E,Id,?> crudService;
    private EntityManager entityManager;


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
    public Set<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastClientFetch, List<QueryFilter<? super E>> jpqlFilters) {
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

    // not very fast, but comfortable if ram filters are needed (EntityFilter)
    @Transactional
    @Override
    public Set<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastClientFetch, List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> entityFilters) {
        // server side update info
        Set<EntitySyncStatus> result = new HashSet<>();
        // todo overwrite and check soft delete timestamp
        List<E> updatedEntities = auditingRepository.findEntitiesLastUpdatedSince(lastClientFetch, jpqlFilters);
        List<E> filtered = FilterUtils.applyMemoryFilters(updatedEntities, entityFilters);
        for (E entity : filtered) {
            if (entity.getLastModifiedDate().after(lastClientFetch)) {
                result.add(
                        EntitySyncStatus.builder()
                                .id(entity.getId().toString())
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
        // maybe add parallel flag ?
        return lastUpdateInfos.stream()
                .map(this::findEntitySyncStatus)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Lazy
    @Autowired
    public void injectCrudService(AbstractCrudService<E, Id,?> crudService) {
        this.crudService = crudService;
    }

    @Autowired
    public void injectEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void injectIdConverter(IdConverter<Id> idConverter) {
        this.idConverter = idConverter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.auditingRepository= new RapidAuditingRepository<>(entityManager,crudService.getEntityClass());
    }
}
