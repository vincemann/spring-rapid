package io.github.vincemann.generic.crud.lib.dto.uniDir;

import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildId;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildIdCollection;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.uniDir.UniDirChild;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownChildTypeException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface UniDirDtoParent {

    Map<Class, Field[]> uniDirChildFieldsCache = new HashMap<>();
    Map<Class, Field[]> uniDirChildrenCollectionFieldsCache = new HashMap<>();


    default <ChildId extends Serializable> ChildId findChildId(Class<? extends UniDirChild> childClazz) throws UnknownChildTypeException, IllegalAccessException {
        Field[] childrenIdFields = findChildrenIdFields();
        for (Field field: childrenIdFields) {
            if(field.getAnnotation(UniDirChildId.class).value().equals(childClazz)) {
                field.setAccessible(true);
                return (ChildId) field.get(this);
            }
        }
        throw new UnknownChildTypeException(this.getClass(),childClazz);
    }

    default <ChildId extends Serializable> Collection<ChildId> findChildrenIdCollection(Class<? extends UniDirChild> childClazz) throws IllegalAccessException {
        Field[] childrenIdCollectionFields = findChildrenIdCollectionFields();
        for (Field field: childrenIdCollectionFields) {
            if(field.getAnnotation(UniDirChildIdCollection.class).value().equals(childClazz)) {
                field.setAccessible(true);
                return (Collection<ChildId>) field.get(this);
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
                childrenIds.put(field.getAnnotation(UniDirChildId.class).value(),id);
            }else {
                System.err.println("Warning: Null id found in UniDirDtoParent "+ this + " for ChildIdField with name: " + field.getName());
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
                childrenIdCollections.put(field.getAnnotation(UniDirChildIdCollection.class).value(),idCollection);
            }else {
               throw new IllegalArgumentException("Null idCollection found in UniDirDtoParent "+ this + " for ChildIdCollectionField with name: " + field.getName());
            }
        }
        return childrenIdCollections;
    }

    /**
     * Adds childs id to {@link UniDirChildIdCollection} or {@link UniDirChildId}, depending on type it belongs to
     * @param child
     */
    default void addChildsId(IdentifiableEntity child) throws IllegalAccessException {
        Serializable biDirChildId = child.getId();
        if(biDirChildId==null){
            throw new IllegalArgumentException("Id from Child must not be null");
        }
        Map<Class, Collection<Serializable>> allChildrenIdCollections = findChildrenIdCollections();
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
        Field[] childrenIdFields = findChildrenIdFields();
        for(Field field: childrenIdFields){
            field.setAccessible(true);
            Class<? extends UniDirChild> clazzBelongingToId = field.getAnnotation(UniDirChildId.class).value();
            if(clazzBelongingToId.equals(child.getClass())){
                Object prevChild = field.get(this);
                if(prevChild!=null){
                    System.err.println("Warning: prevChild was not null -> overriding child:  " + prevChild + " from this parent: " + this);
                }
                field.set(this,biDirChildId);
            }
        }
    }

    default Field[] findChildrenIdCollectionFields(){
        Field[] childrenIdCollectionFieldsFromCache = uniDirChildrenCollectionFieldsCache.get(this.getClass());
        if(childrenIdCollectionFieldsFromCache==null){
            Field[] childrenIdCollectionFields = ReflectionUtils.getAnnotatedDeclaredFields(this.getClass(), UniDirChildIdCollection.class, true);
            uniDirChildrenCollectionFieldsCache.put(this.getClass(),childrenIdCollectionFields);
            return childrenIdCollectionFields;
        }else {
            return childrenIdCollectionFieldsFromCache;
        }
    }

    default Field[] findChildrenIdFields(){
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
