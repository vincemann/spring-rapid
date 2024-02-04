package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.model.audit.IAuditingEntity;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.util.FilterUtils;
import com.github.vincemann.springrapid.sync.model.EntityUpdateInfo;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.LastFetchInfo;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.sync.repo.AuditingRepository;
import com.github.vincemann.springrapid.sync.repo.AuditingRepositoryImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.vincemann.springrapid.core.util.FilterUtils.toSpec;


public abstract class JpaSyncService<E extends IAuditingEntity<Id>, Id extends Serializable>
        implements SyncService<E, Id>, InitializingBean {

    protected IdConverter<Id> idConverter;
    // could not merge my custom repo with jpa repo for some reason, so custom repos are seperated
    // and everything that can be auto impl via jpaRepoInterface is subTypeRequirement for Repo generic type
    protected AuditingRepository<E, Id> auditingRepository;
    protected AbstractCrudService<E, Id,?> crudService;
    protected EntityManager entityManager;

    public JpaSyncService() {

    }

    @Transactional
    @Override
    public EntitySyncStatus findEntitySyncStatus(LastFetchInfo clientLastUpdate) {
        String id = clientLastUpdate.getId();
        Id convertedId = idConverter.toId(id);
        // cant distinguish between removed and has never existed, so just say removed bc I guess the client knows
        // what he is doing
        boolean exists = crudService.getRepository().existsById(convertedId);
        if (!exists) {
            return EntitySyncStatus.builder()
                    .id(id)
                    .status(SyncStatus.REMOVED)
                    .build();
        }
        EntityUpdateInfo lastServerUpdate = auditingRepository.findUpdateInfo(convertedId);
        if (lastServerUpdate == null)
            throw new IllegalArgumentException("Could not find EntityUpdateInfo for existing entity: " + id);
        if (lastServerUpdate.getLastUpdate().after(clientLastUpdate.getLastUpdate())) {
            return EntitySyncStatus.builder()
                    .id(id)
                    .status(SyncStatus.UPDATED)
                    .build();
        } else {
            // no update required
            return null;
        }
    }

    @Transactional
    @Override
    public Set<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastClientFetch,Class<?> dtoClass, List<QueryFilter<? super E>> jpqlFilters) {
        // server side update info
        Set<EntitySyncStatus> result = new HashSet<>();
        // cant find out about removed entities - what has been removed must be evaluated by client by comparing own set
        // + its often not relevant that something was removed, for example if client didnt know about the entity in the first place
        List<EntityUpdateInfo> updateInfosSince = auditingRepository.findUpdateInfosSince(lastClientFetch, toSpec(jpqlFilters));
        for (EntityUpdateInfo lastUpdateInfo : updateInfosSince) {
            result.add(
                    EntitySyncStatus.builder()
                            .id(lastUpdateInfo.getId())
                            .status(SyncStatus.UPDATED)
                            .build());

        }
        return result;
    }

    // not very fast, but comfortable if ram filters are needed (EntityFilter)
    @Transactional
    @Override
    public Set<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastClientFetch,Class<?> dtoClass,  List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> entityFilters) {
        // server side update info
        Set<EntitySyncStatus> result = new HashSet<>();
        // cant find out about removed entities - what has been removed must be evaluated by client by comparing own set
        // + its often not relevant that something was removed, for example if client didnt know about the entity in the first place
        List<E> updatedEntities = auditingRepository.findEntitiesUpdatedSince(lastClientFetch, toSpec(jpqlFilters));
        List<E> filtered = FilterUtils.applyMemoryFilters(updatedEntities, entityFilters);
        for (E entity : filtered) {
            result.add(
                    EntitySyncStatus.builder()
                            .id(entity.getId().toString())
                            .status(SyncStatus.UPDATED)
                            .build());

        }
        return result;
    }

    /**
     * only returns set of {@link EntitySyncStatus} for entities that need update.
     */
    @Transactional
    @Override
    public Set<EntitySyncStatus> findEntitySyncStatuses(Collection<LastFetchInfo> lastFetchInfo) {
        // maybe add parallel flag ?
        return lastFetchInfo.stream()
                .map(this::findEntitySyncStatus)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Lazy
    @Autowired
    public void setRepo(AbstractCrudService<E, Id, ?> crudService) {
        this.crudService = crudService;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setIdConverter(IdConverter<Id> idConverter) {
        this.idConverter = idConverter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.auditingRepository = new AuditingRepositoryImpl(entityManager, crudService.getEntityClass());
    }
}
