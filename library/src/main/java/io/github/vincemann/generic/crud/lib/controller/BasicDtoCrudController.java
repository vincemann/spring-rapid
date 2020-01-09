package io.github.vincemann.generic.crud.lib.controller;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.MappingContext;
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
import org.springframework.lang.Nullable;

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
                Id extends Serializable,
                R extends CrudRepository<E,Id>
        >
            implements DtoCrudController<Id> {

    private CrudService<E,Id,R> crudService;
    private DtoMapperFinder<Id> dtoMapperFinder;
    private MappingContext<Id> mappingContext;
    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];


    public BasicDtoCrudController(MappingContext<Id> mappingContext) {
        this.mappingContext = mappingContext;
    }

    @Autowired
    public void injectCrudService(CrudService<E,Id,R> crudService) {
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
            return ok(findMapperAndMapToDto(optionalEntity.get(),getMappingContext().getFindReturnDtoClass()));
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public ResponseEntity<Collection<IdentifiableEntity<Id>>> findAll() throws EntityMappingException {
        Set<E> all = crudService.findAll();
        Collection<IdentifiableEntity<Id>> dtos = new HashSet<>();
        for (E e : all) {
            dtos.add(findMapperAndMapToDto(e,getMappingContext().getFindAllReturnDtoClass()));
        }
        return ok(dtos);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<? extends IdentifiableEntity<Id>> create(IdentifiableEntity<Id> dto) throws BadEntityException, EntityMappingException {
        E entity = findMapperAndMapToEntity(dto, getMappingContext().getCreateArgDtoClass());
        E savedEntity = crudService.save(entity);
        return new ResponseEntity(findMapperAndMapToDto(savedEntity,getMappingContext().getCreateReturnDtoClass()),
                HttpStatus.OK);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<? extends IdentifiableEntity<Id>> update(IdentifiableEntity<Id> dto, boolean full) throws BadEntityException, EntityMappingException, NoIdException, EntityNotFoundException {
        E entity = findMapperAndMapToEntity(dto, getMappingContext().getUpdateArgDtoClass());
        E updatedEntity = crudService.update(entity,full);
        //no idea why casting is necessary here?
        return new ResponseEntity(findMapperAndMapToDto(updatedEntity,getMappingContext().getUpdateReturnDtoClass()),
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

    private ResponseEntity<Collection<IdentifiableEntity<Id>>> ok(Collection<IdentifiableEntity<Id>> dtoCollection){
        return new ResponseEntity<>(dtoCollection,HttpStatus.OK);
    }

    private ResponseEntity<? extends IdentifiableEntity<Id>> ok(IdentifiableEntity<Id> entity) {
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    public <S extends CrudService<E, Id,R>> S getCastedCrudService(){
        return (S) crudService;
    }

}