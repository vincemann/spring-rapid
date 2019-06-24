package vincemann.github.generic.crud.lib.demo.service.map.abs;


import vincemann.github.generic.crud.lib.model.IdentifiableEntity;
import vincemann.github.generic.crud.lib.service.CrudService;
import vincemann.github.generic.crud.lib.service.exception.BadEntityException;
import vincemann.github.generic.crud.lib.service.exception.EntityNotFoundException;
import vincemann.github.generic.crud.lib.service.exception.NoIdException;

import java.io.Serializable;
import java.util.*;

/**
 * Map-based Impl of {@link CrudService}
 * -> Entities will be stored in a map
 * @param <E>       Managed Entity Type
 * @param <Id>      Id of managed Entity
 */
public abstract class MapService<E extends IdentifiableEntity<Id>, Id extends Serializable> implements CrudService<E,Id> {

    private Map<Id, E> map = new HashMap<>();

    public Set<E> findAll(){
        return new HashSet<>(map.values());
    }

    public Optional<E> findById(Id id)  {
        E entity =  map.get(id);
        if(entity==null){
            return Optional.empty();
        }else {
            return Optional.of(entity);
        }
    }


    @Override
    public E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        if(entity.getId()==null){
            throw new NoIdException();
        }
        E foundEntity = map.get(entity.getId());
        if(foundEntity==null){
            throw new EntityNotFoundException();
        }
        return save(entity);
    }

    public E save(E object) throws  BadEntityException {

        if(object != null) {
            if(object.getId() == null){
                object.setId(getNextId());
            }

            map.put(object.getId(), object);
        } else {
            throw new RuntimeException("Object cannot be null");
        }

        return object;
    }

    public void deleteById(Id id) throws EntityNotFoundException {
        if(map.remove(id)==null){
            throw new EntityNotFoundException("Entity with id: " +id + " was not found");
        }
    }

    public void delete(E object) throws EntityNotFoundException {
        if(!map.entrySet().removeIf(entry -> entry.getValue().equals(object))){
            throw new EntityNotFoundException();
        }
    }

    protected abstract Id getNextId();

    protected Map<Id, E> getMap() {
        return map;
    }
}