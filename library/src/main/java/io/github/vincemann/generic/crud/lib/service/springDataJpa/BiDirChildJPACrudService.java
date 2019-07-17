package io.github.vincemann.generic.crud.lib.service.springDataJpa;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class BiDirChildJPACrudService<E extends IdentifiableEntity<Id> & BiDirChild,Id extends Serializable,R extends JpaRepository<E,Id>> extends JPACrudService<E,Id,R> {

    public BiDirChildJPACrudService(R jpaRepository) {
        super(jpaRepository);
    }

    @Override
    public E update(E entity) throws NoIdException, EntityNotFoundException, BadEntityException {
        try {
            onBiDirChildPreUpdate(entity);
            return super.update(entity);
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings("Duplicates")
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
