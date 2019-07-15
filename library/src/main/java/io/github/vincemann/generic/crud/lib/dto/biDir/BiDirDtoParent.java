package io.github.vincemann.generic.crud.lib.dto.biDir;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownChildTypeException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public interface BiDirDtoParent {

    Map<Class, Field[]> biDirChildFieldsCache = new HashMap<>();
    Map<Class, Field[]> biDirChildrenCollectionFieldsCache = new HashMap<>();


    default <ChildId extends Serializable & Comparable> ChildId findChildId(Class<? extends BiDirChild> childClazz) throws UnknownChildTypeException, IllegalAccessException {
        Field[] childrenIdFields = findChildrenIdFields();
        for (Field field: childrenIdFields) {
            if(field.getAnnotation(BiDirChildId.class).value().equals(childClazz)) {
                field.setAccessible(true);
                return (ChildId) field.get(this);
            }
        }
        throw new UnknownChildTypeException(this.getClass(),childClazz);
    }

    default Map<Class,Serializable> findChildrenIds() throws IllegalAccessException {
        Map<Class,Serializable> childrenIds = new HashMap<>();
        Field[] childIdFields = findChildrenIdFields();
        for(Field field: childIdFields){
            field.setAccessible(true);
            Serializable id = (Serializable) field.get(this);
            if(id!=null) {
                childrenIds.put(field.getAnnotation(BiDirChildId.class).value(),id);
            }else {
                System.err.println("Warning: Null id found in BiDirDtoParent "+ this + " for ChildIdField with name: " + field.getName());
            }
        }
        return childrenIds;
    }

    default Map<Class,Collection<Serializable>> findChildrenIdCollections() throws IllegalAccessException {
        Map<Class,Collection<Serializable>> childrenIdCollections = new HashMap<>();
        Field[] childIdCollectionFields = findChildrenIdCollectionFields();
        for (Field field : childIdCollectionFields) {
            field.setAccessible(true);
            Collection<Serializable> idCollection = (Collection<Serializable>) field.get(this);
            if(idCollection!=null){
                childrenIdCollections.put(field.getAnnotation(BiDirChildIdCollection.class).value(),idCollection);
            }else {
                throw new IllegalArgumentException("Null idCollection found in BiDirDtoParent "+ this + " for ChildIdCollectionField with name: " + field.getName());
            }
        }
        return childrenIdCollections;
    }




    default <ChildId extends Serializable & Comparable> Collection<ChildId> findChildrenIdCollection(Class<? extends BiDirChild> childClazz) throws IllegalAccessException {
        Field[] childrenIdCollectionFields = findChildrenIdCollectionFields();
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
    default void addChildsId(BiDirChild biDirChild) throws IllegalAccessException {
        Serializable biDirChildId = ((IdentifiableEntity) biDirChild).getId();
        if(biDirChildId==null){
            throw new IllegalArgumentException("Id from Child must not be null");
        }
        Map<Class, Collection<Serializable>> allChildrenIdCollections = findChildrenIdCollections();
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
        Field[] childrenIdFields = findChildrenIdFields();
        for(Field field: childrenIdFields){
            field.setAccessible(true);
            Class<? extends BiDirChild> clazzBelongingToId = field.getAnnotation(BiDirChildId.class).value();
            if(clazzBelongingToId.equals(biDirChild.getClass())){
                Object prevChild = field.get(this);
                if(prevChild!=null){
                    System.err.println("Warning: prevChild was not null -> overriding child:  " + prevChild + " from this parent: " + this);
                }
                field.set(this,biDirChildId);
            }
        }
    }

    default Field[] findChildrenIdCollectionFields(){
        Field[] childrenIdCollectionFieldsFromCache = biDirChildrenCollectionFieldsCache.get(this.getClass());
        if(childrenIdCollectionFieldsFromCache==null){
            Field[] childrenIdCollectionFields = ReflectionUtils.getAnnotatedDeclaredFields(this.getClass(), BiDirChildIdCollection.class, true);
            biDirChildrenCollectionFieldsCache.put(this.getClass(),childrenIdCollectionFields);
            return childrenIdCollectionFields;
        }else {
            return childrenIdCollectionFieldsFromCache;
        }
    }

    default Field[] findChildrenIdFields(){
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
