package io.github.vincemann.generic.crud.lib.service.springDataJpa;

import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownParentTypeException;
import io.github.vincemann.generic.crud.lib.util.ListUtils;
import lombok.extern.slf4j.Slf4j;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class BackRefSettingJPACrudService<E extends IdentifiableEntity<Id> & BiDirEntity, Id extends Serializable, R extends JpaRepository<E, Id>> extends JPACrudService<E, Id, R> {
    private Map<Class<? extends IdentifiableEntity>, List<Method>> entityClass_BaseEntityAndCollectionGetters_Cache = new HashMap<>();
    //private Map<Class<? extends IdentifiableEntity>, List<Method>> entityClass_BiDirChildGetters_Cache = new HashMap<>();
    //private Map<Class<? extends IdentifiableEntity>, List<Method>> entityClass_CollectionGetters_Cache = new HashMap<>();
    private List<IdentifiableEntity> entitiesAlreadySeen = new ArrayList<>();

    public BackRefSettingJPACrudService(R jpaRepository, Class<E> entityClazz) {
        super(jpaRepository, entityClazz);
    }

    @Override
    public E save(E entity) throws BadEntityException {
        try {
            //parents collections should be updated, when a new child comes in (in a biDirMapping)
            if (entity instanceof BiDirChild) {
                ((BiDirChild) entity).addChildToParents();
            }
            setBackReferencesOfEntityGraph(entity);
        } catch (IllegalAccessException | InvocationTargetException | UnknownParentTypeException e) {
            entitiesAlreadySeen.clear();
            throw new BiDirRelationManagingException("Backreference of Enitity " + getEntityClazz().getSimpleName() + " could not be set", e);
        }
        entitiesAlreadySeen.clear();
        return super.save(entity);
    }


    //todo max depth param
    private void setBackReferencesOfEntityGraph(IdentifiableEntity rootEntity) throws IllegalAccessException, InvocationTargetException, UnknownParentTypeException {
        Stack<IdentifiableEntity> entitiesToCheck = new Stack<>();
        List<IdentifiableEntity> entitiesToAddToCheckList = new ArrayList<>();
        entitiesToCheck.push(rootEntity);
        do {
            setBackReferencesOfEntityGraph(entitiesToCheck, entitiesToAddToCheckList);
            entitiesToCheck.addAll(entitiesToAddToCheckList);
            entitiesToAddToCheckList.clear();
        } while (!entitiesToCheck.isEmpty());
    }

    private void setBackReferencesOfEntityGraph(Stack<IdentifiableEntity> entitiesToCheck, List<IdentifiableEntity> entitiesToAddToCheckList) throws IllegalAccessException, InvocationTargetException, UnknownParentTypeException {
        while (!entitiesToCheck.empty()) {
            IdentifiableEntity parentEntity = entitiesToCheck.pop();
            if (ListUtils.containsByReference(entitiesAlreadySeen,parentEntity)) {
                //prevent endless cycle
                continue;
            }
            entitiesAlreadySeen.add(parentEntity);
            List<Method> getters = findBaseEntityAndCollectionGetters(parentEntity.getClass());
            for (Method getter : getters) {
                //baseEntities Getter - Class
                // biDirChild Getters - Class
                //Collection Getters - Class
                final Object getterResultObject = getter.invoke(parentEntity);
                if (getterResultObject instanceof IdentifiableEntity) {
                    IdentifiableEntity propertyEntity = (IdentifiableEntity) getterResultObject;
                    entitiesToAddToCheckList.add(propertyEntity);

                    if (getterResultObject instanceof BiDirChild) {
                        //wir haben ein BiDirChild gefunden
                        BiDirChild biDirChild = (BiDirChild) getterResultObject;
                        //parentEntity must be of type BiDirParent, since it has a biDiChild
                        BiDirParent biDirParent = (BiDirParent) parentEntity;
                        biDirChild.findAndSetParentIfNull(biDirParent);

                    }
                } else if (getterResultObject instanceof Collection) {
                    //wir haben eine collection
                    Collection collection = (Collection) getterResultObject;
                    if (!collection.isEmpty()) {
                        for (Object entry : collection) {
                            if (entry instanceof IdentifiableEntity) {
                                entitiesToAddToCheckList.add((IdentifiableEntity) entry);
                                if (entry instanceof BiDirChild) {
                                    //parentEntity must be of type BiDirParent, since it has a biDiChild Collection
                                    BiDirParent biDirParent = (BiDirParent) parentEntity;
                                    ((BiDirChild) entry).findAndSetParentIfNull(biDirParent);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private List<Method> findBaseEntityAndCollectionGetters(Class<? extends IdentifiableEntity> clazz) {
        List<Method> cachedBaseEntityAndCollectionGetters = entityClass_BaseEntityAndCollectionGetters_Cache.get(clazz);
        if (cachedBaseEntityAndCollectionGetters == null) {
            List<Method> baseEntityAndCollectionGetters = new ArrayList<>();
            List<Method> getters = ReflectionUtils.findGetters(clazz);
            for (Method getter : getters) {
                if (IdentifiableEntity.class.isAssignableFrom(getter.getReturnType())|| Collection.class.isAssignableFrom(getter.getReturnType())) {
                    baseEntityAndCollectionGetters.add(getter);
                }
            }
            entityClass_BaseEntityAndCollectionGetters_Cache.put(clazz, baseEntityAndCollectionGetters);
            return baseEntityAndCollectionGetters;
        } else {
            return cachedBaseEntityAndCollectionGetters;
        }
    }

    /*private List<Method> findBiDirChildGetters(Class<? extends IdentifiableEntity> clazz) {
        List<Method> cachedBiDirChildEntityGetters = entityClass_BiDirChildGetters_Cache.get(clazz);
        if (cachedBiDirChildEntityGetters == null) {
            List<Method> biDirChildEntityGetters = new ArrayList<>();
            for (Method method : findBaseEntityAndCollectionGetters(clazz)) {
                if (method.getReturnType().isAssignableFrom(BiDirChild.class)) {
                    biDirChildEntityGetters.add(method);
                    entityClass_BiDirChildGetters_Cache.put(clazz, biDirChildEntityGetters);
                }
            }
            return biDirChildEntityGetters;
        } else {
            return cachedBiDirChildEntityGetters;
        }
    }

    private List<Method> findCollectionGetters(Class<? extends IdentifiableEntity> clazz) {
        List<Method> cachedCollectionGetters = entityClass_CollectionGetters_Cache.get(clazz);
        if (cachedCollectionGetters == null) {
            List<Method> collectionGetters = new ArrayList<>();
            for (Method method : findBaseEntityAndCollectionGetters(clazz)) {
                if (method.getReturnType().isAssignableFrom(Collection.class)) {
                    collectionGetters.add(method);
                    entityClass_CollectionGetters_Cache.put(clazz, collectionGetters);
                }
            }
            return collectionGetters;
        } else {
            return cachedCollectionGetters;
        }
    }*/


}
