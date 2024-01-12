package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.EntityReflectionUtils;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.sync.AuditCollection;
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
 */
// todo maybe need string key for diff situations -> for key x record this subset of collections, and for this key that
    // probably should add key to annotation as value (like with validation groups)
public class AuditCollectionsExtension
        extends BasicServiceExtension<CrudService<AuditingEntity<Serializable>, Serializable>>
        implements GenericCrudServiceExtension<CrudService<AuditingEntity<Serializable>, Serializable>, AuditingEntity<Serializable>, Serializable> {



//    @Override
//    public AuditingEntity<Serializable> softUpdate(AuditingEntity<Serializable> entity) throws EntityNotFoundException, BadEntityException {
//        // could update element collection here - unsupported
//        List<Collection<?>> audited = recordOldCollections(entity.getId(),collectionFieldNames);
//        AuditingEntity<Serializable> result = getNext().softUpdate(entity);
//        detectChanges(result,audited);
//        return result;
//    }



    protected void setUpdated(AuditingEntity<?> entity){
        // updates detected, should also trigger dirty checking so AuditingEntityHandler also sets lastModifiedById
        // https://stackoverflow.com/a/63777063/9027032
        entity.setLastModifiedDate(new Date());
    }

    @Override
    public AuditingEntity<Serializable> partialUpdate(AuditingEntity<Serializable> entity, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        // all collection fields must be marked in fieldsRemoved or propertiesToUpdate ( see next method)
        // only check those if present in collectionFieldNames
        List<String> fieldNamesToRemove = Arrays.asList(fieldsToRemove);
        List<Field> relevantFields = findAuditCollectionFields().stream()
                .filter(f -> fieldNamesToRemove.contains(f.getName()))
                .collect(Collectors.toList());
        List<Collection<?>> audited = recordOldCollections(entity.getId(), relevantFields);
        AuditingEntity<Serializable> result = getNext().partialUpdate(entity,fieldsToRemove);
        detectChanges(result,audited);
        return result;
    }

    @Override
    public AuditingEntity<Serializable> partialUpdate(AuditingEntity<Serializable> update, Set<String> propertiesToUpdate, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        // all collection fields must be marked in fieldsRemoved or propertiesToUpdate ( see next method)
        // only check those if present in collectionFieldNames
        List<String> relevantFieldNames = Arrays.asList(fieldsToRemove);
        relevantFieldNames.addAll(propertiesToUpdate);
        List<Field> fieldNames = findAuditCollectionFields().stream()
                .filter(f -> relevantFieldNames.contains(f.getName()))
                .collect(Collectors.toList());
        List<Collection<?>> audited = recordOldCollections(update.getId(), fieldNames);
        AuditingEntity<Serializable> result = getNext().partialUpdate(update,propertiesToUpdate,fieldsToRemove);
        detectChanges(result,audited);
        return result;
    }

    @Override
    public AuditingEntity<Serializable> fullUpdate(AuditingEntity<Serializable> entity) throws BadEntityException, EntityNotFoundException {
        List<Collection<?>> audited = recordOldCollections(entity.getId(),findAuditCollectionFields());
        AuditingEntity<Serializable> result = getNext().softUpdate(entity);
        detectChanges(result,audited);
        return result;
    }

    protected List<Collection<?>> recordOldCollections(Serializable id, List<Field> collectionFieldNames) throws EntityNotFoundException {
        Optional<AuditingEntity<Serializable>> byId = getLast().findById(id);
        List<Collection<?>> auditedCollections = new ArrayList<>();
        AuditingEntity<Serializable> before = VerifyEntity.isPresent(byId, id, getLast().getEntityClass());
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

    protected void detectChanges(AuditingEntity<Serializable> result, List<Collection<?>> audited) {
        int count = 0;
        for (Collection<?> oldCollection : audited) {
            // old collection is detached
            Collection<?> updatedCollection = accessCollectionField(result, findAuditCollectionFields().get(count));
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
        Class<AuditingEntity<Serializable>> entityClass = getLast().getEntityClass();
        List<Field> cached = cache.get(entityClass);
        if (cached != null)
            return cached;

        List<Field> fields = new ArrayList<>();
        EntityReflectionUtils.doWithAnnotatedFieldsOfType(Collection.class, AuditCollection.class, entityClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                fields.add(field);
            }
        });

        cache.put(entityClass,fields);
        return fields;
    }




}
