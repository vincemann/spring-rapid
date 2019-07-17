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

public abstract class BiDirParentJPACrudService<E extends IdentifiableEntity<Id> & BiDirParent,Id extends Serializable,R extends JpaRepository<E,Id>> extends JPACrudService<E,Id,R> {
    public BiDirParentJPACrudService(R jpaRepository) {
        super(jpaRepository);
    }

    @Override
    public E update(E entity) throws NoIdException, EntityNotFoundException, BadEntityException {
        try {

            onBiDirParentPreUpdate(entity);
            return super.update(entity);
        } catch (IllegalAccessException e) {
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
}
