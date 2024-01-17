package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.model.IAuditingEntity;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.EntityReflectionUtils;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.sync.AuditCollection;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.github.vincemann.springrapid.sync.util.ReflectionUtils.accessCollectionField;
import static com.github.vincemann.springrapid.sync.util.ReflectionUtils.createAndAddAll;

/**
 * Sets {@link AuditingEntity#getLastModifiedDate()} when collection of entity annotated with {@link AuditCollection} is updated.
 * Only works for direct updates!
 */
// todo somehow this is only typesafe with Long and not with Serializable? fix this
// the bounds say ? super Id and Id is inferred as Long. Serializable is a superclass of Long - very weird
// but i dont want anyone to have to cast in order to add this extension
// if string type is used for id, then a copy of this with String type for id is required
public class AuditCollectionsExtension
        extends BasicServiceExtension<CrudService<IAuditingEntity<Long>, Long>>
        implements GenericCrudServiceExtension<CrudService<IAuditingEntity<Long>, Long>, IAuditingEntity<Long>, Long> {



//    @Override
//    public AuditingEntity<Serializable> softUpdate(AuditingEntity<Serializable> entity) throws EntityNotFoundException, BadEntityException {
//        // could update element collection here - unsupported
//        List<Collection<?>> audited = recordOldCollections(entity.getId(),collectionFieldNames);
//        AuditingEntity<Serializable> result = getNext().softUpdate(entity);
//        detectChanges(result,audited);
//        return result;
//    }



    protected void setUpdated(IAuditingEntity<?> entity){
        // updates detected, should also trigger dirty checking so AuditingEntityHandler also sets lastModifiedById
        // https://stackoverflow.com/a/63777063/9027032
        entity.setLastModifiedDate(new Date());
    }

    @Transactional
    @Override
    public IAuditingEntity<Long> partialUpdate(IAuditingEntity<Long> entity, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException {
        // all collection fields must be marked in fieldsRemoved or propertiesToUpdate ( see next method)
        // only check those if present in collectionFieldNames
        List<String> fieldNamesToRemove = Lists.newArrayList(fieldsToUpdate);
        List<Field> auditFields = findAuditCollectionFields().stream()
                .filter(f -> fieldNamesToRemove.contains(f.getName()))
                .collect(Collectors.toList());
        List<Collection<?>> audited = recordOldCollections(entity.getId(), auditFields);
        IAuditingEntity<Long> result = getNext().partialUpdate(entity, fieldsToUpdate);
        detectChanges(result,audited,auditFields);
        return result;
    }


    @Transactional
    @Override
    public IAuditingEntity<Long> fullUpdate(IAuditingEntity<Long> entity) throws BadEntityException, EntityNotFoundException {
        List<Field> auditFields = findAuditCollectionFields();
        List<Collection<?>> audited = recordOldCollections(entity.getId(),auditFields);
        IAuditingEntity<Long> result = getNext().softUpdate(entity);
        detectChanges(result,audited,auditFields);
        return result;
    }

    protected List<Collection<?>> recordOldCollections(Long id, List<Field> collectionFieldNames) throws EntityNotFoundException {
        Optional<IAuditingEntity<Long>> byId = getLast().findById(id);
        List<Collection<?>> auditedCollections = new ArrayList<>();
        IAuditingEntity<Long> before = VerifyEntity.isPresent(byId, id, getLast().getEntityClass());
        for (Field collectionField : collectionFieldNames) {
            // needs to be detached via new Set
            Collection<?> collection = accessCollectionField(before, collectionField);
            if (collection == null)
                auditedCollections.add(null);
            else
                auditedCollections.add(createAndAddAll(collection));
        }
        return auditedCollections;
    }

    protected void detectChanges(IAuditingEntity<Long> result, List<Collection<?>> audited, List<Field> auditFields) {
        int count = 0;
        for (Collection<?> oldCollection : audited) {
            // old collection is detached
            Collection<?> updatedCollection = accessCollectionField(result, auditFields.get(count));
            if (oldCollection == null && updatedCollection == null)
                continue; // no update
            else if (oldCollection == null && updatedCollection != null)
                setUpdated(result);
            else if (oldCollection != null && updatedCollection == null)
                setUpdated(result);
            else{
                if(!oldCollection.equals(updatedCollection)){
                    setUpdated(result);
                }
            }
            count++;
        }
    }

    protected static final ConcurrentMap<Class<?>,List<Field>> cache = new ConcurrentHashMap<>();
    // looks for AuditCollection annotation
    protected List<Field> findAuditCollectionFields(){
        Class<IAuditingEntity<Long>> entityClass = getLast().getEntityClass();
        List<Field> cached = cache.get(entityClass);
        if (cached != null)
            return cached;

        List<Field> fields = new ArrayList<>();
        EntityReflectionUtils.doWithAnnotatedFieldsOfSubType(Collection.class,
                AuditCollection.class, entityClass, fields::add);

        cache.put(entityClass,fields);
        return fields;
    }




}
