package io.github.vincemann.generic.crud.lib.service.springDataJpa;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.*;

public class BiDirParentAndChildJPACrudService <E extends IdentifiableEntity<Id> & BiDirParent & BiDirChild,Id extends Serializable & Comparable,R extends JpaRepository<E,Id>> extends JPACrudService<E,Id,R>  {

    public BiDirParentAndChildJPACrudService(R jpaRepository) {
        super(jpaRepository);
    }

    @Override
    public E update(E entity) throws NoIdException, EntityNotFoundException, BadEntityException {
        try {
            onBiDirParentPreUpdate(entity);
            onBiDirChildPreUpdate(entity);
            return super.update(entity);
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }

    }

    @SuppressWarnings("Duplicates")
    private void onBiDirParentPreUpdate(E newBiDirParent) throws NoIdException, EntityNotFoundException, IllegalAccessException {
        Optional<E> oldBiDirParentOptional = findById(newBiDirParent.getId());
        if(!oldBiDirParentOptional.isPresent()){
            throw new EntityNotFoundException(newBiDirParent.getId(),newBiDirParent.getClass());
        }
        E oldBiDirParent = oldBiDirParentOptional.get();

        Set<? extends BiDirChild> oldChildren = oldBiDirParent.getChildren();
        Set<? extends BiDirChild> newChildren = newBiDirParent.getChildren();

        Set<Collection<? extends BiDirChild>> oldChildrenCollections = oldBiDirParent.getChildrenCollections().keySet();
        Set<Collection<? extends BiDirChild>> newChildrenCollections = newBiDirParent.getChildrenCollections().keySet();

        //find removed Children
        List<BiDirChild> removedChildren = new ArrayList<>();
        for (BiDirChild oldChild : oldChildren) {
            if(!newChildren.contains(oldChild)){
                removedChildren.add(oldChild);
            }
        }
        for (Collection<? extends BiDirChild> oldChildrenCollection : oldChildrenCollections) {
            for (BiDirChild oldChild : oldChildrenCollection) {
                if(!newChildren.contains(oldChild)){
                    removedChildren.add(oldChild);
                }
            }
        }

        //find added Children
        List<BiDirChild> addedChildren = new ArrayList<>();
        for (BiDirChild newChild : newChildren) {
            if(!oldChildren.contains(newChild)){
                addedChildren.add(newChild);
            }
        }

        for (Collection<? extends BiDirChild> newChildrenCollection : newChildrenCollections) {
            for (BiDirChild newChild : newChildrenCollection) {
                if(!oldChildren.contains(newChild)){
                    addedChildren.add(newChild);
                }
            }
        }

        //dismiss removed Children from newParent
        for (BiDirChild removedChild : removedChildren) {
            removedChild.dismissParent(oldBiDirParent);
        }

        //add added Children to newParent
        for (BiDirChild addedChild : addedChildren) {
            addedChild.findAndSetParent(newBiDirParent);
        }
    }

    @SuppressWarnings({"Duplicates"})
    private void onBiDirChildPreUpdate(E newBiDirChild) throws NoIdException, EntityNotFoundException, IllegalAccessException {
        //find already persisted biDirChild (preUpdateState of child)
        Optional<E> oldBiDirChildOptional = findById(newBiDirChild.getId());
        if(!oldBiDirChildOptional.isPresent()){
            throw new EntityNotFoundException(newBiDirChild.getId(),newBiDirChild.getClass());
        }
        E oldBiDirChild = oldBiDirChildOptional.get();
        Collection<BiDirParent> oldParents = oldBiDirChild.findParents();
        Collection<BiDirParent> newParents = newBiDirChild.findParents();
        //find removed parents
        List<BiDirParent> removedParents = new ArrayList<>();
        for (BiDirParent oldParent : oldParents) {
            if(!newParents.contains(oldParent)){
                removedParents.add(oldParent);
            }
        }
        //find added parents
        List<BiDirParent> addedParents = new ArrayList<>();
        for (BiDirParent newParent : newParents) {
            if(!oldParents.contains(newParent)){
                addedParents.add(newParent);
            }
        }

        //dismiss removed Parents Children
        for (BiDirParent removedParent : removedParents) {
            removedParent.dismissChild(newBiDirChild);
        }

        //add added Parent to child
        for (BiDirParent addedParent : addedParents) {
            addedParent.addChild(newBiDirChild);
        }
    }
}
