package io.github.vincemann.generic.crud.lib.controller;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.finder.DtoMapperFinder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.*;


/**
 * Impl of {@link DtoCrudController} that handles the following:
 * Mapping of ServiceEntity to Dto and vice versa.
 * Interaction with specified  {@link CrudService}.
 * Supply hook Methods.
 *
 * @param <E>        Entity Type, of entity, which's curd endpoints are exposed by this Controller
 * @param <Id>       Id Type of {@link E}
 */
@Slf4j
@Getter
@Setter
public abstract class BasicDtoCrudController
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable
        >
            implements DtoCrudController<Id> {

    private CrudService<E,Id,? extends CrudRepository<E,Id>> crudService;
    private DtoMapperFinder<Id> dtoMapperFinder;
    private DtoMappingContext<Id> dtoMappingContext;
    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];


    public BasicDtoCrudController(DtoMappingContext<Id> dtoMappingContext) {
        this.dtoMappingContext = dtoMappingContext;
    }

    @Autowired
    public void injectCrudService(CrudService<E,Id,? extends CrudRepository<E,Id>> crudService) {
        this.crudService = crudService;
    }

    @Autowired
    public void injectDtoMapperFinder(DtoMapperFinder<Id> dtoMapperFinder) {
        this.dtoMapperFinder = dtoMapperFinder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<? extends IdentifiableEntity<Id>> find(Id id) throws NoIdException, EntityNotFoundException, EntityMappingException {
        Optional<E> optionalEntity = crudService.findById(id);
        if (optionalEntity.isPresent()) {
            return ok(findMapperAndMapToDto(optionalEntity.get(), getDtoMappingContext().getFindReturnDtoClass()));
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public ResponseEntity<Collection<IdentifiableEntity<Id>>> findAll() throws EntityMappingException {
        Set<E> all = crudService.findAll();
        Collection<IdentifiableEntity<Id>> dtos = new HashSet<>();
        for (E e : all) {
            dtos.add(findMapperAndMapToDto(e, getDtoMappingContext().getFindAllReturnDtoClass()));
        }
        return ok(dtos);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<? extends IdentifiableEntity<Id>> create(IdentifiableEntity<Id> dto) throws BadEntityException, EntityMappingException {
        E entity = findMapperAndMapToEntity(dto, getDtoMappingContext().getCreateRequestDtoClass());
        E savedEntity = crudService.save(entity);
        return new ResponseEntity(findMapperAndMapToDto(savedEntity, getDtoMappingContext().getCreateReturnDtoClass()),
                HttpStatus.OK);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<? extends IdentifiableEntity<Id>> update(IdentifiableEntity<Id> dto, boolean full) throws BadEntityException, EntityMappingException, NoIdException, EntityNotFoundException {
        E entity = findMapperAndMapToEntity(dto, getDtoMappingContext().getPartialUpdateRequestDtoClass());
        E updatedEntity = crudService.update(entity,full);
        //no idea why casting is necessary here?
        return new ResponseEntity(findMapperAndMapToDto(updatedEntity, getDtoMappingContext().getUpdateReturnDtoClass()),
                HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> delete(Id id) throws NoIdException, EntityNotFoundException {
        crudService.deleteById(id);
        return ResponseEntity.ok().build();
    }


    public  <Dto extends IdentifiableEntity<Id>> Dto findMapperAndMapToDto(E entity, Class<Dto> dtoClass) throws EntityMappingException {
        DtoMapper dtoMapper = dtoMapperFinder.find(dtoClass);
        return dtoMapper.mapEntityToDto(entity,dtoClass);
    }

    public E findMapperAndMapToEntity(IdentifiableEntity<Id> dto, Class<? extends IdentifiableEntity<Id>> dtoClass) throws EntityMappingException {
        Class dtoClazz;
        if(dtoClass==null){
            dtoClazz=dto.getClass();
        }else {
            dtoClazz = dtoClass;
        }
        DtoMapper dtoMapper = dtoMapperFinder.find(dtoClazz);
        return dtoMapper.mapDtoToEntity(dto, entityClass);
    }

    protected ResponseEntity<Collection<IdentifiableEntity<Id>>> ok(Collection<IdentifiableEntity<Id>> dtoCollection){
        return new ResponseEntity<>(dtoCollection,HttpStatus.OK);
    }

    protected ResponseEntity<? extends IdentifiableEntity<Id>> ok(IdentifiableEntity<Id> entity) {
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    public <S extends CrudService<E, Id,? extends CrudRepository<E,Id>>> S getCastedCrudService(){
        return (S) crudService;
    }

}