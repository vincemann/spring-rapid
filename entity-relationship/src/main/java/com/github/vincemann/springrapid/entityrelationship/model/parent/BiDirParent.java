package com.github.vincemann.springrapid.entityrelationship.model.parent;

import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Represents a parent of a bidirectional relationship (i.e. Entity with @OneToMany typically would implement this interface).
 * The Child of the relationship should implement {@link BiDirChild} and annotate its parents with {@link BiDirParentEntity}.
 */
public interface BiDirParent extends  DirParent {
    Logger log = LoggerFactory.getLogger(BiDirParent.class);

//    Map<Class,Field[]> biDirChildrenCollectionFieldsCache = new HashMap<>();
//    Map<Class,Field[]> biDirChildEntityFieldsCache = new HashMap<>();



//    @Override
//    default void destroy() throws Exception {
//        this.dismissChildrensParent();
//    }

    /**
     * Find the BiDirChildren Collections (all fields of this parent annotated with {@link BiDirChildCollection} )
     * and the Type of the Entities in the Collection.
     * @return
     */
    default Map<Collection<BiDirChild>,Class<BiDirChild>> findAllBiDirChildCollections(){
        return findAllChildCollections(BiDirChildCollection.class);

//        Field[] collectionFields =findChildrenCollectionFields();
//        for(Field field : collectionFields){
//            field.setAccessible(true);
//            Collection<? extends BiDirChild> biDirChildren = (Collection<? extends BiDirChild>) field.get(this);
//            if(biDirChildren == null){
//                //throw new IllegalArgumentException("Null idCollection found in BiDirParent "+ this + " for ChildCollectionField with name: " + field.getName());
//                log.warn("Auto-generating Collection for nullIdCollection Field: " + field);
//                Collection emptyCollection = CollectionUtils.createEmptyCollection(field);
//                field.set(this,emptyCollection);
//                biDirChildren=emptyCollection;
//            }
//            Class<? extends BiDirChild> collectionEntityType = field.getAnnotation(BiDirChildCollection.class).value();
//            childCollection_childTypeMap.put(biDirChildren,collectionEntityType);
//        }

//        return childCollection_childTypeMap;
    }

    /**
     * Add a new Child to this parent.
     * Call this, when saving a {@link BiDirChild} of this parent.
     * child will be added to fields with {@link BiDirChildCollection} and fields with {@link BiDirChildEntity} will be set with newChild, when most specific type matches of newChild matches the field.
     * Child wont be added and UnknownChildTypeException will be thrown when corresponding {@link BiDirChildCollection} is null.
     * @param newChild
     * @throws UnknownChildTypeException
     */
   default void addBiDirChild(BiDirChild newChild) throws UnknownChildTypeException{
       addChild(newChild, BiDirChildEntity.class, BiDirChildCollection.class);

//       Field[] entityFields = findChildrenEntityFields();
//       for(Field childField: entityFields) {
//           if (childField.getType().equals(newChild.getClass())) {
//               childField.setAccessible(true);
//               BiDirChild oldChild = (BiDirChild) childField.get(this);
//               if (oldChild != null) {
//                    log.warn("Overriding old child: "+oldChild+" with new Child "+newChild+" of parent "+this);
//               }
//               childField.set(this,newChild);
//               addedChild.set(true);
//           }
//       }

//       if(!addedChild.get()){
//           throw new UnknownChildTypeException(getClass(),newChild.getClass());
//       }
   }





    /*default Field[] findChildrenCollectionFields(){
        return ReflectionUtils.getAnnotatedDeclaredFieldsAssignableFrom(this.getClass(),BiDirChildSet.class, Collection.class,true);
    }

    default Collection<Collection> getChildrenCollections(Field[] childrenSetFields)  {
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
     */
    default void dismissBiDirChild(BiDirChild biDirChildToRemove) throws UnknownChildTypeException{
        dismissChild(biDirChildToRemove,BiDirChildEntity.class,BiDirChildCollection.class);
//        for(Field childField : findChildrenEntityFields()){
//            childField.setAccessible(true);
//            BiDirChild child = (BiDirChild) childField.get(this);
//            if(child!=null) {
//                if (child.getClass().equals(biDirChildToRemove.getClass())) {
//                    childField.set(this, null);
//                    deletedChild.set(true);
//                }
//            }
//        }
//        if(!deletedChild.get()){
//            throw new UnknownChildTypeException(this.getClass(), biDirChildToRemove.getClass());
//        }
    }

//    default Field[] findChildrenCollectionFields(){
//        Field[] childrenCollectionFieldsFromCache = biDirChildrenCollectionFieldsCache.get(this.getClass());
//        if(childrenCollectionFieldsFromCache==null){
//            Field[] childrenCollectionFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(this.getClass(),BiDirChildCollection.class);
//            biDirChildrenCollectionFieldsCache.put(this.getClass(),childrenCollectionFields);
//            return childrenCollectionFields;
//        }else {
//            return childrenCollectionFieldsFromCache;
//        }
//    }

//    default Field[] findChildrenEntityFields(){
//        Field[] childEntityFieldsFromCache = biDirChildEntityFieldsCache.get(this.getClass());
//        if(childEntityFieldsFromCache==null){
//            Field[] childEntityFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(this.getClass(),BiDirChildEntity.class);
//            biDirChildEntityFieldsCache.put(this.getClass(),childEntityFields);
//            return childEntityFields;
//        }else {
//            return childEntityFieldsFromCache;
//        }
//
//    }



    /**
     * Find the single BiDirChildren (all fields of this parent annotated with {@link BiDirChildEntity} and not null.
     * @return
     */
    default Set<BiDirChild> findBiDirSingleChildren(){
       return findSingleChildren(BiDirChildEntity.class);

//        Field[] entityFields = findChildrenEntityFields();
//        for(Field field: entityFields){
//            field.setAccessible(true);
//            BiDirChild biDirChild = (BiDirChild) field.get(this);
//            if(biDirChild == null){
//                //skip
//                continue;
//            }
//            children.add(biDirChild);
//        }
//        return children;
    }

    /**
     * All children {@link BiDirChild} of this parent wont know about this parent, after this operation.
     * Clear all {@link BiDirChildCollection}s of this parent.
     * Call this, before you want to delete this parent.
     * @throws UnknownParentTypeException
     */
    default void dismissChildrensParent() throws UnknownParentTypeException{
        for(BiDirChild child: findBiDirSingleChildren()){
            child.dismissBiDirParent(this);
        }
        for(Map.Entry<Collection<BiDirChild>,Class<BiDirChild>> entry: findAllBiDirChildCollections().entrySet()){
            Collection<BiDirChild> childrenCollection = entry.getKey();
            for(BiDirChild biDirChild: childrenCollection){
                biDirChild.dismissBiDirParent(this);
            }
            childrenCollection.clear();
        }
    }
}
