package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildEntity;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.UniDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.UniDirChildEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.autobidir.dto.RelationalDtoType;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.BiDirChildId;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.UniDirChildIdCollection;
import com.github.vincemann.springrapid.autobidir.dto.parent.annotation.BiDirParentId;
import com.github.vincemann.springrapid.autobidir.dto.parent.annotation.BiDirParentIdCollection;
import com.github.vincemann.springrapid.autobidir.util.EntityIdAnnotationUtils;
import com.github.vincemann.springrapid.autobidir.util.EntityReflectionUtils;
import com.github.vincemann.springrapid.core.util.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.*;

@Slf4j
public class RapidRelationalDtoManager implements RelationalDtoManager {


    @Cacheable(value = "dtoRelationTypes")
    @Override
    public Set<RelationalDtoType> inferTypes(Class<?> entityClass) {
        Set<RelationalDtoType> relationalEntityTypes = new HashSet<>();
        org.springframework.util.ReflectionUtils.doWithFields(entityClass, field -> {
            org.springframework.util.ReflectionUtils.makeAccessible(field);
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {

                if (annotation.annotationType().equals(BiDirChildId.class)){
                    relationalEntityTypes.add(RelationalDtoType.BiDirParentDto);
                }
                if (annotation.annotationType().equals(BiDirChildIdCollection.class)){
                    relationalEntityTypes.add(RelationalDtoType.BiDirParentDto);
                }
                

                if (annotation.annotationType().equals(BiDirParentId.class)){
                    relationalEntityTypes.add(RelationalDtoType.BiDirChildDto);
                }
                if (annotation.annotationType().equals(BiDirParentIdCollection.class)){
                    relationalEntityTypes.add(RelationalDtoType.BiDirChildDto);
                }


                if (annotation.annotationType().equals(UniDirChildId.class)){
                    relationalEntityTypes.add(RelationalDtoType.UniDirParentDto);
                }
                if (annotation.annotationType().equals(UniDirChildIdCollection.class)){
                    relationalEntityTypes.add(RelationalDtoType.UniDirParentDto);
                }
            }

        }/*, field -> IdentifiableEntity.class.isAssignableFrom(field.getType())*/);
        return relationalEntityTypes;
    }

    



    // UNI DIR PARENT METHODS





    //    public <ChildId extends Serializable> ChildId findUniDirChildId(Class<? extends UniDirChild> childClazz) throws UnknownChildTypeException {
//        return findChildId(childClazz,UniDirChildId.class);
//    }

    public Map<Class<IdentifiableEntity>, Serializable> findUniDirChildIds(Object parent){
        return findEntityIds(parent,UniDirChildId.class);
    }



//    public <ChildId extends Serializable> Collection<ChildId> findUniDirChildIdCollection(Class<? extends UniDirChild> childClazz) {
//        return findChildIdCollection(childClazz,UniDirChildIdCollection.class);
//    }



    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findUniDirChildIdCollections(Object parent){
        return findEntityIdCollections(parent,UniDirChildIdCollection.class);
    }


    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findAllUniDirChildIds(Object parent){
        return findAllEntityIds(parent, UniDirChildId.class, UniDirChildIdCollection.class);
    }

    /**
     * Adds child's id to {@link UniDirChildIdCollection} or {@link UniDirChildId}, depending on entity type it belongs to
     *
     * @param child
     */
    public void addUniDirChildId(IdentifiableEntity child, Object parent) {
        addEntityId(child,parent, UniDirChildId.class, UniDirChildIdCollection.class);
    }

//    public Field[] findUniDirChildrenIdCollectionFields() {
//        Field[] childrenIdCollectionFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(this.getClass(), UniDirChildIdCollection.class);
//        return childrenIdCollectionFields;
//    }
//
//    public Field[] findUniDirChildrenIdFields() {
//        Field[] childrenIdFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(getClass(), UniDirChildId.class);
//        return childrenIdFields;
//    }
    



    // BIDIR PARENT METHODS




    public Map<Class<IdentifiableEntity>, Serializable> findBiDirChildIds(Object parent) {
        return findEntityIds(parent,BiDirChildId.class);
    }

//    public <ChildId extends Serializable> Collection<ChildId> findBiDirChildrenIdCollection(Class<? extends IdentifiableEntity> childClazz)  {
//        return findChildIdCollection(childClazz,BiDirChildIdCollection.class);
//    }

    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findBiDirChildIdCollections(Object parent) {
        return findEntityIdCollections(parent,BiDirChildIdCollection.class);
    }

    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findAllBiDirChildIds(Object parent){
        return findAllEntityIds(parent, BiDirChildId.class, BiDirChildIdCollection.class);
    }




    /**
     * Adds childs id to {@link BiDirChildIdCollection} or {@link BiDirChildId}, depending on type it belongs to
     *
     * @param child
     */
    // adds childs id to parent
    public void addBiDirChildId(IdentifiableEntity child, Object parent) {
        addEntityId(child,parent, BiDirChildId.class, BiDirChildIdCollection.class);
    }
    
    
    // BIDIR CHILD METHODS

    public Map<Class<IdentifiableEntity>, Serializable> findBiDirParentIds(Object child) {
        return findEntityIds(child,BiDirParentId.class);
    }

    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findBiDirParentIdCollections(Object child) {
        return findEntityIdCollections(child,BiDirParentIdCollection.class);
    }

    // adds parents id to child
    public void addBiDirParentId(IdentifiableEntity parent, Object child) {
        addEntityId(parent, child,BiDirParentId.class,BiDirParentIdCollection.class);
    }

    public Map<Class<IdentifiableEntity>, Collection<Serializable>> findAllBiDirParentIds(Object parent){
        return findAllEntityIds(parent, BiDirParentId.class, BiDirParentIdCollection.class);
    }



    
    // CORE METHODS




    protected <C> Map<Class<C>, Serializable> findEntityIds(Object entity, Class<? extends Annotation> entityIdAnnotationType) {
        final Map<Class<C>, Serializable> result = new HashMap<>();
        EntityReflectionUtils.doWithAnnotatedFields(entityIdAnnotationType, entity.getClass(), field -> {
            Serializable id = (Serializable) field.get(entity);
            if (id != null) {
                result.put((Class<C>) EntityIdAnnotationUtils.getEntityType(field.getAnnotation(entityIdAnnotationType)), id);
            } else {
                log.warn("Null id found in BiDirParentDto " + entity + " for EntityIdField with name: " + field.getName());
            }
        });
        return result;
    }

    protected <C> Map<Class<C>, Collection<Serializable>> findEntityIdCollections(Object entity,Class<? extends Annotation> entityCollectionIdAnnotationType) {
        final Map<Class<C>, Collection<Serializable>> result = new HashMap<>();
        EntityReflectionUtils.doWithAnnotatedFields(entityCollectionIdAnnotationType,entity.getClass(),field -> {
            Collection<Serializable> idCollection = (Collection<Serializable>) field.get(entity);
            if (idCollection != null) {
                result.put((Class<C>) EntityIdAnnotationUtils.getEntityType(field.getAnnotation(entityCollectionIdAnnotationType)), idCollection);
            }/*else {
               throw new IllegalArgumentException("Null idCollection found in UniDirParentDto "+ this + " for ChildIdCollectionField with name: " + field.getName());
            }*/
        });
        return result;
    }


    protected Map<Class<IdentifiableEntity>, Collection<Serializable>> findAllEntityIds(Object entity,Class<? extends Annotation> entityIdAnnotationType, Class<? extends Annotation> entityCollectionIdAnnotationType){
        Map<Class<IdentifiableEntity>, Collection<Serializable>> entityIdCollections = findEntityIdCollections(entity,entityCollectionIdAnnotationType);
        Map<Class<IdentifiableEntity>, Serializable> uniDirChildIds = findEntityIds(entity,entityIdAnnotationType);
        uniDirChildIds.forEach((clazz, id) -> entityIdCollections.put(clazz,new HashSet<>(Lists.newArrayList(id))));
        return entityIdCollections;
    }



    protected void addEntityId(IdentifiableEntity src, Object target, Class<? extends Annotation> entityIdAnnotationClass, Class<? extends Annotation> entityIdCollectionAnnotationClass) {
        Serializable entityId = src.getId();
        if (entityId == null) {
            throw new IllegalArgumentException("EntityId must not be null");
        }
        Map<Class<IdentifiableEntity>, Collection<Serializable>> entityIdCollections = findEntityIdCollections(target,entityIdCollectionAnnotationClass);
        //child collections
        for (Map.Entry<Class<IdentifiableEntity>, Collection<Serializable>> entityIdCollectionEntry : entityIdCollections.entrySet()) {
            if (entityIdCollectionEntry.getKey().equals(src.getClass())) {
                //need to add
                Collection<Serializable> idCollection = entityIdCollectionEntry.getValue();
                //dirChild is always an Identifiable Child
                idCollection.add(entityId);
            }
        }

        EntityReflectionUtils.doWithIdFieldsWithEntityType(src.getClass(), entityIdAnnotationClass, target.getClass(), field -> {
            Object prevEntityId = field.get(target);
            if (prevEntityId != null) {
                log.warn("Warning, prev EntityId: " + prevEntityId + " was not null -> overriding with new value: " + entityId);
            }
            field.set(target, entityId);
        });
    }
}
