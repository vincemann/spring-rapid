package vincemann.github.generic.crud.lib.service.springDataJpa;

import vincemann.github.generic.crud.lib.model.IdentifiableEntity;
import vincemann.github.generic.crud.lib.model.biDir.BiDirChild;
import vincemann.github.generic.crud.lib.service.exception.EntityNotFoundException;
import vincemann.github.generic.crud.lib.service.exception.NoIdException;
import vincemann.github.generic.crud.lib.service.exception.UnknownChildTypeException;
import vincemann.github.generic.crud.lib.service.exception.UnknownParentTypeException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.Optional;

public class BiDirChildBackRefSettingJPACrudService<E extends IdentifiableEntity<Id> & BiDirChild, Id extends Serializable, R extends JpaRepository<E, Id>> extends BackRefSettingJPACrudService<E,Id,R> {


    public BiDirChildBackRefSettingJPACrudService(R jpaRepository, Class<E> entityClazz) {
        super(jpaRepository, entityClazz);
    }

    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        //only needs to be handled specifically when entity is a biDirChild
        try {
            entity.dismissParents();
        }catch (UnknownChildTypeException | UnknownParentTypeException|IllegalAccessException e){
            throw new BiDirRelationManagingException(e);
        }
        super.delete(entity);
    }

    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        //only needs to be handled specifically when entity is a biDirChild
        if(id==null){
            super.deleteById(id);
            return;
        }
        Optional<E> entityToDelete  = getJpaRepository().findById(id);
        if(entityToDelete.isPresent()) {
            try {
                entityToDelete.get().dismissParents();
            }catch (UnknownChildTypeException | UnknownParentTypeException|IllegalAccessException e){
                throw new BiDirRelationManagingException(e);
            }
        }
        super.deleteById(id);
    }
}
