package com.github.vincemann.springrapid.sync.softdelete;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.LastFetchInfo;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.sync.service.SyncService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;


/**
 * Soft delete impl of {@link SyncService} utilizing {@link SoftDeleteRepository} instead of {@link com.github.vincemann.springrapid.sync.repo.AuditingRepository}.
 * @param <E> entity type
 * @param <Id> id type of entity
 */
public abstract class SoftDeleteSyncService<E extends ISoftDeleteEntity<Id>, Id extends Serializable>
    implements SyncService<E,Id>, InitializingBean
{
    private SoftDeleteAuditingRepository<E,Id> auditingRepository;

    private EntityManager entityManager;
    private IdConverter<Id> idConverter;
    private CrudRepository<E,Id> repository;
    private Class<E> entityClass;

    public SoftDeleteSyncService() {
        this.entityClass = provideEntityClass();
    }

    @Override
    public void afterPropertiesSet() {
        this.auditingRepository = provideAuditingRepository();
    }

    /**
     * could also overwrite this method with empty impl and create autowired setter method.
     * @Bean
     * public AuditingRepository<Foo,Long> fooRepository(){...}
     */
    protected SoftDeleteAuditingRepository<E,Id> provideAuditingRepository(){
        return new SoftDeleteAuditingRepositoryImpl<>(entityManager,entityClass);
    }

    protected Class<E> provideEntityClass(){
        return (Class<E>) GenericTypeResolver.resolveTypeArguments(this.getClass(), SoftDeleteSyncService.class)[0];
    }

    @Transactional(readOnly = true)
    @Override
    @Nullable
    public EntitySyncStatus findEntitySyncStatus(LastFetchInfo lastClientUpdate) throws EntityNotFoundException {
        String id = lastClientUpdate.getId();
        Id convertedId = idConverter.toId(id);
        SoftDeleteEntityUpdateInfo lastServerUpdate = auditingRepository.findUpdateInfo(convertedId);
        if (lastServerUpdate == null){
            // means entity has never existed
            throw new EntityNotFoundException("Could not find EntityUpdateInfo for entity: " + id);
        }
        if (lastServerUpdate.getDeletedDate() != null){
            if (lastServerUpdate.getDeletedDate().after(lastClientUpdate.getLastUpdate())){
                return new EntitySyncStatus(id,SyncStatus.REMOVED);
            }else{
                // no update required, still removed and user knows its removed
                return null;
            }
        }
        if (lastServerUpdate.getLastUpdate().after(lastClientUpdate.getLastUpdate())) {
            return new EntitySyncStatus(id,SyncStatus.UPDATED);
        } else {
            // no update required, entity still in same state as last time client fetched
            return null;
        }
    }

    /**
     * @see com.github.vincemann.springrapid.sync.service.DefaultSyncService#findEntitySyncStatusesSinceTimestamp(Timestamp, Specification)
     */
    @Transactional(readOnly = true)
    @Override
    public List<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastClientFetch) {
        return findEntitySyncStatusesSinceTimestamp(lastClientFetch,null);
    }

    protected List<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastClientFetch, @Nullable Specification<E> specification){
        // server side update info
        List<EntitySyncStatus> result = new ArrayList<>();
        // returns all entitys removed or updated since ts
        List<SoftDeleteEntityUpdateInfo> updateInfosSince = auditingRepository.findUpdateInfosSince(lastClientFetch, specification);
        for (SoftDeleteEntityUpdateInfo updateInfo : updateInfosSince) {
            // need to check if updated or removed - it must be one or the other
            boolean removed = updateInfo.getDeletedDate() != null;
            if (removed){
                result.add(new EntitySyncStatus(updateInfo.getId(),SyncStatus.REMOVED));
            }else{
                result.add(new EntitySyncStatus(updateInfo.getId(),SyncStatus.UPDATED));
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EntitySyncStatus> findEntitySyncStatuses(Collection<LastFetchInfo> lastFetchInfo) throws EntityNotFoundException {
        // maybe add parallel flag ?
        List<EntitySyncStatus> result = new ArrayList<>();
        for (LastFetchInfo fetchInfo : lastFetchInfo) {
            EntitySyncStatus syncStatus = findEntitySyncStatus(fetchInfo);
            if (syncStatus != null)
                result.add(syncStatus);
        }
        return result;
    }

    protected SoftDeleteAuditingRepository<E, Id> getAuditingRepository() {
        return auditingRepository;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected IdConverter<Id> getIdConverter() {
        return idConverter;
    }

    protected CrudRepository<E, Id> getRepository() {
        return repository;
    }

    protected Class<E> getEntityClass() {
        return entityClass;
    }

    @Autowired
    public void setIdConverter(IdConverter<Id> idConverter) {
        this.idConverter = idConverter;
    }

    @Autowired
    public void setRepository(CrudRepository<E, Id> repository) {
        this.repository = repository;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
