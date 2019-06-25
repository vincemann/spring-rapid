package io.github.vincemann.generic.crud.lib.model.biDir;

import io.github.vincemann.generic.crud.lib.service.exception.UnknownChildTypeException;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownParentTypeException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

//muss ich als interface machen, weil es entities geben wird die gleichzeitig child und parent entity sind
public interface BiDirParent extends BiDirEntity {

    Map<Class,Field[]> biDirChildrenCollectionFieldsCache = new HashMap<>();

    default void dismissChildren() throws UnknownParentTypeException, IllegalAccessException {
        for(Map.Entry<Collection<? extends BiDirChild>,Class<? extends BiDirChild>> entry: getChildrenCollections().entrySet()){
            Collection<? extends BiDirChild> childrenCollection = entry.getKey();
            for(BiDirChild biDirChild: childrenCollection){
                biDirChild.dismissParent(this);
            }
            childrenCollection.clear();
        }
    }

   default void addChild(BiDirChild biDirChild) throws UnknownChildTypeException, IllegalAccessException {
       AtomicBoolean addedChildToAtLeastOneCollection = new AtomicBoolean(false);
       for(Map.Entry entry: getChildrenCollections().entrySet()){
           Class targetClass = (Class) entry.getValue();
           if(biDirChild.getClass().equals(targetClass)){
               ((Collection)entry.getKey()).add(biDirChild);
               addedChildToAtLeastOneCollection.set(true);
           }
       }

       if(!addedChildToAtLeastOneCollection.get()){
           throw new UnknownChildTypeException(getClass(),biDirChild.getClass());
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

    default void dismissChild(BiDirChild biDirChildToRemove) throws UnknownChildTypeException, IllegalAccessException {
        AtomicBoolean deletedFromAtLeastOneChildSet = new AtomicBoolean(false);
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
                                deletedFromAtLeastOneChildSet.set(true);
                            }
                        }
                    });
                }
            }
        }
        if(!deletedFromAtLeastOneChildSet.get()){
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
    /**
     * give me the BiDirChildren Collections and their Type
     * @return
     */
    default Map<Collection<? extends BiDirChild>,Class<? extends BiDirChild>> getChildrenCollections() throws IllegalAccessException {
        Map<Collection<? extends BiDirChild>,Class<? extends BiDirChild>> childrenCollection_CollectionTypeMap = new HashMap<>();
        Field[] fields =findChildrenCollectionFields();
        for(Field field : fields){
            field.setAccessible(true);
            Collection<? extends BiDirChild> biDirChildren = (Collection<? extends BiDirChild>) field.get(this);
            if(biDirChildren == null){
                //skip
                continue;
            }
            Class<? extends BiDirChild> collectionType = field.getAnnotation(BiDirChildCollection.class).value();
            childrenCollection_CollectionTypeMap.put(biDirChildren,collectionType);
        }
        return childrenCollection_CollectionTypeMap;
    }
}
