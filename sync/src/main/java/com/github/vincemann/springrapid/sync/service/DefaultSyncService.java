package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.sync.model.entity.IAuditingEntity;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.EntityUpdateInfo;
import com.github.vincemann.springrapid.sync.model.LastFetchInfo;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.sync.repo.AuditingRepository;
import com.github.vincemann.springrapid.sync.repo.AuditingRepositoryImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Default impl of {@link SyncService}.
 * Uses {@link AuditingRepository} for getting sync status information and uses {@link CrudRepository} for existence checks.
 *
 * @param <E> entity type, must implement {@link IAuditingEntity}
 * @param <Id> id type of entity
 *
 * @see this#findEntitySyncStatusesSinceTimestamp(Timestamp, Specification)
 * @see SyncService
 */
public abstract class DefaultSyncService
        <
                E extends IAuditingEntity<Id>,
                Id extends Serializable>
        implements SyncService<E, Id>, InitializingBean {

    private IdConverter<Id> idConverter;
    private AuditingRepository<E, Id> auditingRepository;

    private CrudRepository<E,Id> repository;
    private EntityManager entityManager;

    private Class<E> entityClass;

    public DefaultSyncService() {
        this.entityClass = provideEntityClass();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.auditingRepository = provideAuditingRepository();
    }

    protected Class<E> provideEntityClass(){
        return (Class<E>) GenericTypeResolver.resolveTypeArguments(this.getClass(), DefaultSyncService.class)[0];
    }

    /**
     * could also overwrite this method and annotate with @Autowired when bean is defined in context like:
     * @Bean
     * public AuditingRepository<Foo,Long> fooRepository(){...}
     */
    protected AuditingRepository<E,Id> provideAuditingRepository(){
        return new AuditingRepositoryImpl<>(entityManager, entityClass);
    }

    @Transactional(readOnly = true)
    @Override
    @Nullable
    public EntitySyncStatus findEntitySyncStatus(LastFetchInfo lastClientUpdate) {
        String id = lastClientUpdate.getId();
        Id convertedId = idConverter.toId(id);
        // cant distinguish between removed and has never existed, so just say removed bc I guess the client knows
        // what he is doing
        boolean exists = repository.existsById(convertedId);
        if (!exists) {
            return new EntitySyncStatus(id,SyncStatus.REMOVED);
        }
        EntityUpdateInfo lastServerUpdate = auditingRepository.findUpdateInfo(convertedId);
        Assert.notNull(lastServerUpdate,"Could not find EntityUpdateInfo for existing entity: " + id);
        if (lastServerUpdate.getLastUpdate().after(lastClientUpdate.getLastUpdate())) {
            return new EntitySyncStatus(id,SyncStatus.UPDATED);
        } else {
            // no update required
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastClientFetch) {
        return findEntitySyncStatusesSinceTimestamp(lastClientFetch,null);
    }

    /**
     * Call this method in implementations of this class, that define methods that wish to add filters.
     * Example:
     *
     *  @Override
     *  public List<EntitySyncStatus> findEntitySyncStatusesSinceTimestampOfSubscribedOfModule(Timestamp lastClientUpdate, long moduleId, String user) {
     *      Specification<ExerciseGroup> spec = new WithModuleId(moduleId).and(new Subscribed(user));
     *      return findEntitySyncStatusesSinceTimestamp(lastClientUpdate,spec);
     *  }
     *
     *   @AllArgsConstructor
     *   private static class WithModuleId implements Specification<ExerciseGroup> {
     *
     *       private Long moduleId;
     *       @Nullable
     *       @Override
     *       public Predicate toPredicate(Root<ExerciseGroup> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
     *            return cb.equal(root.<Module>get("module").get("id"), moduleId);
     *       }
     *   }
     *     ...
     *
     */
    protected List<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastClientFetch, @Nullable Specification<E> specification) {
        // server side update info
        List<EntitySyncStatus> result = new ArrayList<>();
        // cant find out about removed entities - what has been removed must be evaluated by client by comparing own set
        // + its often not relevant that something was removed, for example if client didnt know about the entity in the first place
        List<E> updatedEntities = auditingRepository.findEntitiesUpdatedSince(lastClientFetch,specification);
        updatedEntities.stream().map(e -> new EntitySyncStatus(e.getId().toString(),SyncStatus.UPDATED)).collect(Collectors.toSet());
        for (E entity : updatedEntities) {
            result.add(new EntitySyncStatus(entity.getId().toString(), SyncStatus.UPDATED));

        }
        return result;
    }

    /**
     * Only returns set of {@link EntitySyncStatus} for entities that need update or are removed.
     */
    @Transactional(readOnly = true)
    @Override
    public List<EntitySyncStatus> findEntitySyncStatuses(Collection<LastFetchInfo> lastFetchInfo) {
        return lastFetchInfo.stream()
                .map(this::findEntitySyncStatus)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Autowired
    public void setRepository(JpaRepository<E, Id> repository) {
        this.repository = repository;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setIdConverter(IdConverter<Id> idConverter) {
        this.idConverter = idConverter;
    }


    protected IdConverter<Id> getIdConverter() {
        return idConverter;
    }

    protected AuditingRepository<E, Id> getAuditingRepository() {
        return auditingRepository;
    }

    protected CrudRepository<E, Id> getRepository() {
        return repository;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected Class<E> getEntityClass() {
        return entityClass;
    }
}
