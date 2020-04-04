package io.github.vincemann.springrapid.core.dto.uniDir;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.model.uniDir.child.UniDirChild;
import io.github.vincemann.springrapid.core.service.exception.entityRelationHandling.UnknownChildTypeException;
import io.github.vincemann.springrapid.core.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface UniDirParentDto {
    Logger log = LoggerFactory.getLogger(UniDirParentDto.class);

    Map<Class, Field[]> uniDirChildFieldsCache = new HashMap<>();
    Map<Class, Field[]> uniDirChildrenCollectionFieldsCache = new HashMap<>();


    default <ChildId extends Serializable> ChildId findUniDirChildId(Class<? extends UniDirChild> childClazz) throws UnknownChildTypeException, IllegalAccessException {
        Field[] childrenIdFields = findUniDirChildrenIdFields();
        for (Field field: childrenIdFields) {
            if(field.getAnnotation(UniDirChildId.class).value().equals(childClazz)) {
                field.setAccessible(true);
                return (ChildId) field.get(this);
            }
        }
        throw new UnknownChildTypeException(this.getClass(),childClazz);
    }

    default <ChildId extends Serializable> Collection<ChildId> findUniDirChildrenIdCollection(Class<? extends UniDirChild> childClazz) throws IllegalAccessException {
        Field[] childrenIdCollectionFields = findUniDirChildrenIdCollectionFields();
        for (Field field: childrenIdCollectionFields) {
            if(field.getAnnotation(UniDirChildIdCollection.class).value().equals(childClazz)) {
                field.setAccessible(true);
                return (Collection<ChildId>) field.get(this);
            }
        }
        throw new UnknownChildTypeException(this.getClass(),childClazz);
    }

    default Map<Class,Serializable> findUniDirChildrenIds() throws IllegalAccessException {
        Map<Class,Serializable> childrenIds = new HashMap<>();
        Field[] childIdFields = findUniDirChildrenIdFields();
        for(Field field: childIdFields){
            field.setAccessible(true);
            Serializable id = (Serializable) field.get(this);
            if(id!=null) {
                childrenIds.put(field.getAnnotation(UniDirChildId.class).value(),id);
            }else {
                log.warn("Null id found in UniDirParentDto "+ this + " for ChildIdField with name: " + field.getName());
            }
        }
        return childrenIds;
    }

    default Map<Class,Collection<Serializable>> findUniDirChildrenIdCollections() throws IllegalAccessException {
        Map<Class,Collection<Serializable>> childrenIdCollections = new HashMap<>();
        Field[] childIdCollectionFields = findUniDirChildrenIdCollectionFields();
        for (Field field : childIdCollectionFields) {
            field.setAccessible(true);
            Collection<Serializable> idCollection = (Collection<Serializable>) field.get(this);
            if(idCollection!=null){
                childrenIdCollections.put(field.getAnnotation(UniDirChildIdCollection.class).value(),idCollection);
            }/*else {
               throw new IllegalArgumentException("Null idCollection found in UniDirParentDto "+ this + " for ChildIdCollectionField with name: " + field.getName());
            }*/
        }
        return childrenIdCollections;
    }

    /**
     * Adds child's id to {@link UniDirChildIdCollection} or {@link UniDirChildId}, depending on entity type it belongs to
     * @param child
     */
    default void addUniDirChildsId(IdentifiableEntity child) throws IllegalAccessException {
        Serializable biDirChildId = child.getId();
        if(biDirChildId==null){
            throw new IllegalArgumentException("Id from Child must not be null");
        }
        Map<Class, Collection<Serializable>> allChildrenIdCollections = findUniDirChildrenIdCollections();
        //child collections
        for(Map.Entry<Class,Collection<Serializable>> childrenIdCollectionEntry : allChildrenIdCollections.entrySet()){
            if(childrenIdCollectionEntry.getKey().equals(child.getClass())){
                //need to add
                Collection<Serializable> idCollection = childrenIdCollectionEntry.getValue();
                //biDirChild is always an Identifiable Child
                idCollection.add(biDirChildId);
            }
        }
        //single children
        Field[] childrenIdFields = findUniDirChildrenIdFields();
        for(Field field: childrenIdFields){
            field.setAccessible(true);
            Class<? extends UniDirChild> clazzBelongingToId = field.getAnnotation(UniDirChildId.class).value();
            if(clazzBelongingToId.equals(child.getClass())){
                Object prevChild = field.get(this);
                if(prevChild!=null){
                    log.warn("Warning: previous Child was not null -> overriding child:  " + prevChild + " from this parent: " + this + " with new value: " + child);
                }
                field.set(this,biDirChildId);
            }
        }
    }

    default Field[] findUniDirChildrenIdCollectionFields(){
        Field[] childrenIdCollectionFieldsFromCache = uniDirChildrenCollectionFieldsCache.get(this.getClass());
        if(childrenIdCollectionFieldsFromCache==null){
            Field[] childrenIdCollectionFields = ReflectionUtils.getAnnotatedDeclaredFields(this.getClass(), UniDirChildIdCollection.class, true);
            uniDirChildrenCollectionFieldsCache.put(this.getClass(),childrenIdCollectionFields);
            return childrenIdCollectionFields;
        }else {
            return childrenIdCollectionFieldsFromCache;
        }
    }

    default Field[] findUniDirChildrenIdFields(){
        Field[] childrenIdFieldsFromCache = uniDirChildFieldsCache.get(this.getClass());
        if(childrenIdFieldsFromCache==null){
            Field[] childrenIdFields = ReflectionUtils.getDeclaredFieldsAnnotatedWith(getClass(), UniDirChildId.class, true);
            uniDirChildFieldsCache.put(this.getClass(),childrenIdFields);
            return childrenIdFields;
        }else {
            return childrenIdFieldsFromCache;
        }
    }
}
