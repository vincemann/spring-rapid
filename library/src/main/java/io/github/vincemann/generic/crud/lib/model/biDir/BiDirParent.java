package io.github.vincemann.generic.crud.lib.model.biDir;

import io.github.vincemann.generic.crud.lib.service.exception.UnknownChildTypeException;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownParentTypeException;
import io.github.vincemann.generic.crud.lib.util.CollectionUtils;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

//muss ich als interface machen, weil es entities geben wird die gleichzeitig child und parent entity sind

/**
 * represents a parent of a bidirectional jpa relationship (i.e. Entity with @OneToMany typically would implement this interface)
 * the Child of the Relation ship should implement {@link BiDirChild} and annotate its parents with {@link BiDirParentEntity}
 */
public interface BiDirParent extends BiDirEntity {

    Map<Class,Field[]> biDirChildrenCollectionFieldsCache = new HashMap<>();
    Map<Class,Field[]> biDirChildEntityFieldsCache = new HashMap<>();

    /**
     * All children {@link BiDirChild} of this parent wont know about this parent, after this operation.
     * Clear all {@link BiDirChildCollection}s of this parent.
     * Call this, before you want to delete this parent.
     * @throws UnknownParentTypeException
     * @throws IllegalAccessException
     */
    default void dismissChildrensParent() throws UnknownParentTypeException, IllegalAccessException {
        for(BiDirChild child: getChildren()){
            child.dismissParent(this);
        }
        for(Map.Entry<Collection<? extends BiDirChild>,Class<? extends BiDirChild>> entry: getChildrenCollections().entrySet()){
            Collection<? extends BiDirChild> childrenCollection = entry.getKey();
            for(BiDirChild biDirChild: childrenCollection){
                biDirChild.dismissParent(this);
            }
            childrenCollection.clear();
        }
    }

    /**
     * Add a new Child to this parent.
     * Call this, when saving a {@link BiDirChild} of this parent.
     * child will be added to fields with {@link BiDirChildCollection} and fields with {@link BiDirChildEntity} will be set with newChild, when most specific type matches of newChild matches the field.
     * Child wont be added and UnknownChildTypeException will be thrown when corresponding {@link BiDirChildCollection} is null.
     * @param newChild
     * @throws UnknownChildTypeException
     * @throws IllegalAccessException
     */
   default void addChild(BiDirChild newChild) throws UnknownChildTypeException, IllegalAccessException {
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
               BiDirChild oldChild = (BiDirChild) childField.get(this);
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
        return ReflectionUtils.getAnnotatedDeclaredFieldsAssignableFrom(this.getClass(),BiDirChildSet.class, Collection.class,true);
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
     * This parent wont know about the given biDirChildToRemove after this operation.
     * Call this, before you delete the biDirChildToRemove.
     * Case 1: Remove Child {@link BiDirChild} from all {@link BiDirChildCollection}s from this parent.
     * Case 2: Set {@link BiDirChildEntity}Field to null if child is not saved in a collection in this parent.
     * @param biDirChildToRemove
     * @throws UnknownChildTypeException
     * @throws IllegalAccessException
     */
    default void dismissChild(BiDirChild biDirChildToRemove) throws UnknownChildTypeException, IllegalAccessException {
        AtomicBoolean deletedChild = new AtomicBoolean(false);
        for(Map.Entry<Collection<? extends BiDirChild>,Class<? extends BiDirChild>> entry: getChildrenCollections().entrySet()){
            Collection<? extends BiDirChild> childrenCollection = entry.getKey();
            if(childrenCollection!=null){
                if(!childrenCollection.isEmpty()){
                    Optional<? extends BiDirChild> optionalBiDirChild = childrenCollection.stream().findFirst();
                    optionalBiDirChild.ifPresent(child -> {
                        if(biDirChildToRemove.getClass().equals(child.getClass())){
                            //this set needs to remove the child
                            boolean successfulRemove = childrenCollection.remove(biDirChildToRemove);
                            if(!successfulRemove){
                                System.err.println("Entity: "+ biDirChildToRemove + " was not present in children set of parent: " + this);
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
            BiDirChild child = (BiDirChild) childField.get(this);
            if(child!=null) {
                if (child.getClass().equals(biDirChildToRemove.getClass())) {
                    childField.set(this, null);
                    deletedChild.set(true);
                }
            }
        }
        if(!deletedChild.get()){
            throw new UnknownChildTypeException(this.getClass(), biDirChildToRemove.getClass());
        }
    }

    default Field[] findChildrenCollectionFields(){
        Field[] childrenCollectionFieldsFromCache = biDirChildrenCollectionFieldsCache.get(this.getClass());
        if(childrenCollectionFieldsFromCache==null){
            Field[] childrenCollectionFields = ReflectionUtils.getDeclaredFieldsAnnotatedWith(this.getClass(),BiDirChildCollection.class,true);
            biDirChildrenCollectionFieldsCache.put(this.getClass(),childrenCollectionFields);
            return childrenCollectionFields;
        }else {
            return childrenCollectionFieldsFromCache;
        }
    }

    default Field[] findChildrenEntityFields(){
        Field[] childEntityFieldsFromCache = biDirChildEntityFieldsCache.get(this.getClass());
        if(childEntityFieldsFromCache==null){
            Field[] childEntityFields = ReflectionUtils.getDeclaredFieldsAnnotatedWith(this.getClass(),BiDirChildEntity.class,true);
            biDirChildEntityFieldsCache.put(this.getClass(),childEntityFields);
            return childEntityFields;
        }else {
            return childEntityFieldsFromCache;
        }

    }
    /**
     * Find the BiDirChildren Collections (all fields of this parent annotated with {@link BiDirChildCollection} and not null )
     * and the Type of the Entities in the Collection.
     * @return
     */
    default Map<Collection<? extends BiDirChild>,Class<? extends BiDirChild>> getChildrenCollections() throws IllegalAccessException {
        Map<Collection<? extends BiDirChild>,Class<? extends BiDirChild>> childrenCollection_CollectionTypeMap = new HashMap<>();
        Field[] collectionFields =findChildrenCollectionFields();

        for(Field field : collectionFields){
            field.setAccessible(true);
            Collection<? extends BiDirChild> biDirChildren = (Collection<? extends BiDirChild>) field.get(this);
            if(biDirChildren == null){
                //skip
                continue;
            }
            Class<? extends BiDirChild> collectionEntityType = field.getAnnotation(BiDirChildCollection.class).value();
            childrenCollection_CollectionTypeMap.put(biDirChildren,collectionEntityType);
        }

        return childrenCollection_CollectionTypeMap;
    }


    /**
     * Find the single BiDirChildren (all fields of this parent annotated with {@link BiDirChildEntity} and not null.
     * @return
     * @throws IllegalAccessException
     */
    default Set<? extends BiDirChild> getChildren() throws IllegalAccessException {
        Set<BiDirChild> children = new HashSet<>();
        Field[] entityFields = findChildrenEntityFields();
        for(Field field: entityFields){
            field.setAccessible(true);
            BiDirChild biDirChild = (BiDirChild) field.get(this);
            if(biDirChild == null){
                //skip
                continue;
            }
            children.add(biDirChild);
        }
        return children;
    }
}
