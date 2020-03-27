//package io.github.vincemann.generic.crud.lib.service.plugin;
//
//import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
//import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
//import io.github.vincemann.generic.crud.lib.model.biDir.child.BiDirChild;
//import io.github.vincemann.generic.crud.lib.model.biDir.parent.BiDirParent;
//import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
//import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.Serializable;
//import java.util.*;
//
//@ServiceComponent
//@Slf4j
//@Transactional
///**
// * Analog to {@link BiDirChildPlugin}.
// */
//public class BiDirParentPlugin<E extends IdentifiableEntity<Id> & BiDirParent,Id extends Serializable>
//        extends CrudServicePlugin<E,Id> {
//
//
////    private EntityMangerSessionReattacher entityMangerSessionReattacher;
////
////    @Autowired
////    @Lazy
////    public void setEntityMangerSessionReattacher(EntityMangerSessionReattacher entityMangerSessionReattacher) {
////        this.entityMangerSessionReattacher = entityMangerSessionReattacher;
////    }
//
//    public void onBeforeUpdate(E entity, boolean full) throws EntityNotFoundException, NoIdException {
//        try {
//            handleChildCollectionChanges(entity);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
////    public void onBeforeSave(E entityToSave){
////        try {
////            attachChildrenToCurrentSessionIfNecessary(entityToSave);
////        } catch (IllegalAccessException e) {
////            throw new RuntimeException(e);
////        }
////    }
//
////
////    //todo shouldnt this be done by SessionReattachmentPlugin
////    private void attachChildrenToCurrentSessionIfNecessary(E entityToSave) throws IllegalAccessException{
////        //iterate over all children of this parent (entityToSave)
////        Map<Collection<? extends BiDirChild>, Class<? extends BiDirChild>> childrenCollections = entityToSave.getChildrenCollections();
////        for (Map.Entry<Collection<? extends BiDirChild>, Class<? extends BiDirChild>> childrenCollectionEntry : childrenCollections.entrySet()) {
////            Collection<? extends BiDirChild> childrenCollection = childrenCollectionEntry.getKey();
////            for (BiDirChild child : childrenCollection) {
////                Serializable childId = ((IdentifiableEntity) child).getId();
////                //if child has id, then it is already persisted and detached to current session started by service, so it needs to be reattached
////                if(childId!=null) {
////                    entityMangerSessionReattacher.attachToCurrentSession(child);
////                    //THIS WOULD BE AN ALTERNATIVE SOLUTION TO ATTACH THE CHILD, BUT ITS SHIT
////                        //CrudService service = crudServices.get(biDirChild.getClass());
////                        //Optional optionalChildFromService = service.findById(childId);
////                        //Object childFromService = optionalChildFromService.get();
////                        //replace child (possibly detached) with same child found by service (is now attached to the session)
////                        //entityToSave.dismissChild(biDirChild);
////                        //entityToSave.addChild((BiDirChild) childFromService);
////                }
////                //if child has no id, then we dont have a problem, it will be persisted within this create-entity-session started by service
////            }
////        }
////    }
//
//
//    @SuppressWarnings("Duplicates")
//    private void handleChildCollectionChanges(E newBiDirParent) throws NoIdException, EntityNotFoundException, IllegalAccessException {
//        Optional<E> oldBiDirParentOptional = getService().findById(newBiDirParent.getId());
//        if(!oldBiDirParentOptional.isPresent()){
//            throw new EntityNotFoundException(newBiDirParent.getId(),newBiDirParent.getClass());
//        }
//        E oldBiDirParent = oldBiDirParentOptional.get();
//
//        Set<? extends BiDirChild> oldChildren = oldBiDirParent.getChildren();
//        Set<? extends BiDirChild> newChildren = newBiDirParent.getChildren();
//
//        Set<Collection<? extends BiDirChild>> oldChildrenCollections = oldBiDirParent.getChildrenCollections().keySet();
//        Set<Collection<? extends BiDirChild>> newChildrenCollections = newBiDirParent.getChildrenCollections().keySet();
//
//        //find removed Children
//        List<BiDirChild> removedChildren = new ArrayList<>();
//        for (BiDirChild oldChild : oldChildren) {
//            if(!newChildren.contains(oldChild)){
//                removedChildren.add(oldChild);
//            }
//        }
//        for (Collection<? extends BiDirChild> oldChildrenCollection : oldChildrenCollections) {
//            for (BiDirChild oldChild : oldChildrenCollection) {
//                if(!newChildren.contains(oldChild)){
//                    removedChildren.add(oldChild);
//                }
//            }
//        }
//
//        //find added Children
//        List<BiDirChild> addedChildren = new ArrayList<>();
//        for (BiDirChild newChild : newChildren) {
//            if(!oldChildren.contains(newChild)){
//                addedChildren.add(newChild);
//            }
//        }
//
//        for (Collection<? extends BiDirChild> newChildrenCollection : newChildrenCollections) {
//            for (BiDirChild newChild : newChildrenCollection) {
//                if(!oldChildren.contains(newChild)){
//                    addedChildren.add(newChild);
//                }
//            }
//        }
//
//        //dismiss removed Children from newParent
//        for (BiDirChild removedChild : removedChildren) {
//            log.debug("dismissing child: " + removedChild + " from parent: " +newBiDirParent);
//            removedChild.dismissParent(oldBiDirParent);
//        }
//
//        //add added Children to newParent
//        for (BiDirChild addedChild : addedChildren) {
//            log.debug("adding child: " + addedChild + " to parent: " +newBiDirParent);
//            addedChild.setParentRef(newBiDirParent);
//        }
//    }
//}
