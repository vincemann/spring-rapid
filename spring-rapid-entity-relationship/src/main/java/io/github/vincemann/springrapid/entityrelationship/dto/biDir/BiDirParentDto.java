package io.github.vincemann.springrapid.entityrelationship.dto.biDir;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.entityrelationship.model.biDir.child.BiDirChild;
import io.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import io.github.vincemann.springrapid.core.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public interface BiDirParentDto {

    Logger log = LoggerFactory.getLogger(BiDirParentDto.class);
    Map<Class, Field[]> biDirChildFieldsCache = new HashMap<>();
    Map<Class, Field[]> biDirChildrenCollectionFieldsCache = new HashMap<>();


    default <ChildId extends Serializable> ChildId findBiDirChildId(Class<? extends BiDirChild> childClazz) throws UnknownChildTypeException, IllegalAccessException {
        Field[] childrenIdFields = findBiDirChildrenIdFields();
        for (Field field: childrenIdFields) {
            if(field.getAnnotation(BiDirChildId.class).value().equals(childClazz)) {
                field.setAccessible(true);
                return (ChildId) field.get(this);
            }
        }
        throw new UnknownChildTypeException(this.getClass(),childClazz);
    }

    default Map<Class,Serializable> findBiDirChildrenIds() throws IllegalAccessException {
        Map<Class,Serializable> childrenIds = new HashMap<>();
        Field[] childIdFields = findBiDirChildrenIdFields();
        for(Field field: childIdFields){
            field.setAccessible(true);
            Serializable id = (Serializable) field.get(this);
            if(id!=null) {
                childrenIds.put(field.getAnnotation(BiDirChildId.class).value(),id);
            }else {
                log.warn("Warning: Null id found in BiDirDtoParent "+ this + " for ChildIdField with name: " + field.getName());
            }
        }
        return childrenIds;
    }

    default Map<Class,Collection<Serializable>> findBiDirChildrenIdCollections() throws IllegalAccessException {
        Map<Class,Collection<Serializable>> childrenIdCollections = new HashMap<>();
        Field[] childIdCollectionFields = findBiDirChildrenIdCollectionFields();
        for (Field field : childIdCollectionFields) {
            field.setAccessible(true);
            Collection<Serializable> idCollection = (Collection<Serializable>) field.get(this);
            if(idCollection!=null){
                childrenIdCollections.put(field.getAnnotation(BiDirChildIdCollection.class).value(),idCollection);
            }/*else {
                throw new IllegalArgumentException("Null idCollection found in BiDirDtoParent "+ this + " for ChildIdCollectionField with name: " + field.getName());
            }*/
        }
        return childrenIdCollections;
    }




    default <ChildId extends Serializable> Collection<ChildId> findBiDirChildrenIdCollection(Class<? extends BiDirChild> childClazz) throws IllegalAccessException {
        Field[] childrenIdCollectionFields = findBiDirChildrenIdCollectionFields();
        for (Field field: childrenIdCollectionFields) {
            if(field.getAnnotation(BiDirChildIdCollection.class).value().equals(childClazz)) {
                field.setAccessible(true);
                return (Collection<ChildId>) field.get(this);
            }
        }
        throw new UnknownChildTypeException(this.getClass(),childClazz);
    }

    /**
     * Adds childs id to {@link BiDirChildIdCollection} or {@link BiDirChildId}, depending on type it belongs to
     * @param biDirChild
     */
    default void addBiDirChildsId(BiDirChild biDirChild) throws IllegalAccessException {
        Serializable biDirChildId = ((IdentifiableEntity) biDirChild).getId();
        if(biDirChildId==null){
            throw new IllegalArgumentException("Id from Child must not be null");
        }
        Map<Class, Collection<Serializable>> allChildrenIdCollections = findBiDirChildrenIdCollections();
        //child collections
        for(Map.Entry<Class,Collection<Serializable>> childrenIdCollectionEntry : allChildrenIdCollections.entrySet()){
            if(childrenIdCollectionEntry.getKey().equals(biDirChild.getClass())){
                //need to add
                Collection<Serializable> idCollection = childrenIdCollectionEntry.getValue();
                //biDirChild is always an Identifiable Child
                idCollection.add(biDirChildId);
            }
        }
        //single children
        Field[] childrenIdFields = findBiDirChildrenIdFields();
        for(Field field: childrenIdFields){
            field.setAccessible(true);
            Class<? extends BiDirChild> clazzBelongingToId = field.getAnnotation(BiDirChildId.class).value();
            if(clazzBelongingToId.equals(biDirChild.getClass())){
                Object prevChild = field.get(this);
                if(prevChild!=null){
                    log.warn("Warning: prevChild was not null -> overriding child:  " + prevChild + " from this parent: " + this);
                }
                field.set(this,biDirChildId);
            }
        }
    }

    default Field[] findBiDirChildrenIdCollectionFields(){
        Field[] childrenIdCollectionFieldsFromCache = biDirChildrenCollectionFieldsCache.get(this.getClass());
        if(childrenIdCollectionFieldsFromCache==null){
            Field[] childrenIdCollectionFields = ReflectionUtils.getAnnotatedDeclaredFields(this.getClass(), BiDirChildIdCollection.class, true);
            biDirChildrenCollectionFieldsCache.put(this.getClass(),childrenIdCollectionFields);
            return childrenIdCollectionFields;
        }else {
            return childrenIdCollectionFieldsFromCache;
        }
    }

    default Field[] findBiDirChildrenIdFields(){
        Field[] childrenIdFieldsFromCache = biDirChildFieldsCache.get(this.getClass());
        if(childrenIdFieldsFromCache==null){
            Field[] childrenIdFields = ReflectionUtils.getDeclaredFieldsAnnotatedWith(getClass(), BiDirChildId.class, true);
            biDirChildFieldsCache.put(this.getClass(),childrenIdFields);
            return childrenIdFields;
        }else {
            return childrenIdFieldsFromCache;
        }
    }
}
