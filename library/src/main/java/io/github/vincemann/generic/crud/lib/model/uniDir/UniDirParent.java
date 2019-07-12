package io.github.vincemann.generic.crud.lib.model.uniDir;

import io.github.vincemann.generic.crud.lib.service.exception.UnknownChildTypeException;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownParentTypeException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public interface UniDirParent extends UniDirEntity {

    Map<Class, Field[]> uniDirChildrenCollectionFieldsCache = new HashMap<>();
    Map<Class,Field[]> uniDirChildEntityFieldsCache = new HashMap<>();


    /**
     * Add a new Child to this parent.
     * Call this, when saving a {@link UniDirChild} of this parent.
     * child will be added to fields with {@link UniDirChildCollection} and fields with {@link UniDirChildEntity} will be set with newChild, when most specific type matches of newChild matches the field.
     * Child wont be added and UnknownChildTypeException will be thrown when corresponding {@link UniDirChildCollection} is null.
     * @param newChild
     * @throws UnknownChildTypeException
     * @throws IllegalAccessException
     */
    default void addChild(Object newChild) throws UnknownChildTypeException, IllegalAccessException {
        AtomicBoolean addedChild = new AtomicBoolean(false);
        for(Map.Entry entry: getChildrenCollections().entrySet()){
            Class targetClass = (Class) entry.getValue();
            if(newChild.getClass().equals(targetClass)){
                ((Collection)entry.getKey()).add(newChild);
                addedChild.set(true);
            }
        }

        Field[] entityFields = findChildrenEntityFields();
        for(Field childField: entityFields) {
            if (childField.getType().equals(newChild.getClass())) {
                childField.setAccessible(true);
                Object oldChild = childField.get(this);
                if (oldChild != null) {
                    System.err.println("warning, overriding old child: " + oldChild + " with new Child: " + newChild + " of parent: " + this);
                }
                childField.set(this,newChild);
                addedChild.set(true);
            }
        }

        if(!addedChild.get()){
            throw new UnknownChildTypeException(getClass(),newChild.getClass());
        }
    }

    /*default Field[] findChildrenCollectionFields(){
        return ReflectionUtils.getAnnotatedDeclaredFieldsAssignableFrom(this.getClass(),UniDirChildSet.class, Collection.class,true);
    }

    default Collection<Collection> getChildrenCollections(Field[] childrenSetFields) throws IllegalAccessException {
        Collection<Collection> childrenCollections = new ArrayList<>();
        for(Field childrenSetField : childrenSetFields){
            childrenSetField.setAccessible(true);
            Collection childrenCollection = (Collection) childrenSetField.get(this);
            childrenCollections.add(childrenCollection);
        }
        return childrenCollections;
    }*/

    /**
     * This parent wont know about the given uniDirChildToRemove after this operation.
     * Call this, before you delete the uniDirChildToRemove.
     * Case 1: Remove Child {@link UniDirChild} from all {@link UniDirChildCollection}s from this parent.
     * Case 2: Set {@link UniDirChildEntity}Field to null if child is not saved in a collection in this parent.
     * @param uniDirChildToRemove
     * @throws UnknownChildTypeException
     * @throws IllegalAccessException
     */
    default void dismissChild(Object uniDirChildToRemove) throws UnknownChildTypeException, IllegalAccessException {
        AtomicBoolean deletedChild = new AtomicBoolean(false);
        for(Map.Entry<Collection,Class<? extends UniDirChild>> entry: getChildrenCollections().entrySet()){
            Collection<? extends UniDirChild> childrenCollection = entry.getKey();
            if(childrenCollection!=null){
                if(!childrenCollection.isEmpty()){
                    Optional<? extends UniDirChild> optionalUniDirChild = childrenCollection.stream().findFirst();
                    optionalUniDirChild.ifPresent(child -> {
                        if(uniDirChildToRemove.getClass().equals(child.getClass())){
                            //this set needs to remove the child
                            boolean successfulRemove = childrenCollection.remove(uniDirChildToRemove);
                            if(!successfulRemove){
                                System.err.println("Entity: "+ uniDirChildToRemove + " was not present in children set of parent: " + this);
                            }else {
                                deletedChild.set(true);
                            }
                        }
                    });
                }
            }
        }
        for(Field childField : findChildrenEntityFields()){
            childField.setAccessible(true);
            UniDirChild child = (UniDirChild) childField.get(this);
            if(child!=null) {
                if (child.getClass().equals(uniDirChildToRemove.getClass())) {
                    childField.set(this, null);
                    deletedChild.set(true);
                }
            }
        }
        if(!deletedChild.get()){
            throw new UnknownChildTypeException(this.getClass(), uniDirChildToRemove.getClass());
        }
    }

    default Field[] findChildrenCollectionFields(){
        Field[] childrenCollectionFieldsFromCache = uniDirChildrenCollectionFieldsCache.get(this.getClass());
        if(childrenCollectionFieldsFromCache==null){
            Field[] childrenCollectionFields = ReflectionUtils.getDeclaredFieldsAnnotatedWith(this.getClass(),UniDirChildCollection.class,true);
            uniDirChildrenCollectionFieldsCache.put(this.getClass(),childrenCollectionFields);
            return childrenCollectionFields;
        }else {
            return childrenCollectionFieldsFromCache;
        }
    }

    default Field[] findChildrenEntityFields(){
        Field[] childEntityFieldsFromCache = uniDirChildEntityFieldsCache.get(this.getClass());
        if(childEntityFieldsFromCache==null){
            Field[] childEntityFields = ReflectionUtils.getDeclaredFieldsAnnotatedWith(this.getClass(),UniDirChildEntity.class,true);
            uniDirChildEntityFieldsCache.put(this.getClass(),childEntityFields);
            return childEntityFields;
        }else {
            return childEntityFieldsFromCache;
        }

    }
    /**
     * Find the UniDirChildren Collections (all fields of this parent annotated with {@link UniDirChildCollection} and not null )
     * and the Type of the Entities in the Collection.
     * @return
     */
    default Map<Collection,Class<? extends UniDirChild>> getChildrenCollections() throws IllegalAccessException {
        Map<Collection,Class<? extends UniDirChild>> childrenCollection_CollectionTypeMap = new HashMap<>();
        Field[] collectionFields =findChildrenCollectionFields();

        for(Field field : collectionFields){
            field.setAccessible(true);
            Collection uniDirChildren = (Collection) field.get(this);
            if(uniDirChildren == null){
                throw new IllegalArgumentException("Null idCollection found in UniDirParent "+ this + " for ChildCollectionField with name: " + field.getName());
            }
            Class<? extends UniDirChild> collectionEntityType = field.getAnnotation(UniDirChildCollection.class).value();
            childrenCollection_CollectionTypeMap.put(uniDirChildren,collectionEntityType);
        }

        return childrenCollection_CollectionTypeMap;
    }


    /**
     * Find the single UniDirChildren (all fields of this parent annotated with {@link UniDirChildEntity} and not null.
     * @return
     * @throws IllegalAccessException
     */
    default Set getChildren() throws IllegalAccessException {
        Set children = new HashSet<>();
        Field[] entityFields = findChildrenEntityFields();
        for(Field field: entityFields){
            field.setAccessible(true);
            Object child = field.get(this);
            if(child == null){
                //skip
                continue;
            }
            children.add(child);
        }
        return children;
    }
}
