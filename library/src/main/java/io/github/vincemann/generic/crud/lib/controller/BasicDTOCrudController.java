package io.github.vincemann.generic.crud.lib.controller;

import io.github.vincemann.generic.crud.lib.controller.exception.EntityMappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.github.vincemann.generic.crud.lib.dtoMapper.DTOMapper;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;

import java.io.Serializable;
import java.util.Optional;


/**
 * Impl of {@link DTOCrudController} that handles the following:
 * Mapping of ServiceEntity to DTO and vice versa.
 * Interaction with specified  {@link CrudService}.
 * Supply callback Methods.
 *
 * @param <ServiceE> Service Entity Type, of entity, which curd enpoints are exposed by this Controller
 * @param <Service>  Service Type of {@link ServiceE}
 * @param <DTO>      DTO Type corresponding to {@link ServiceE}
 * @param <Id>       Id Type of {@link ServiceE}
 */
public abstract class BasicDTOCrudController<ServiceE extends IdentifiableEntity<Id>, Id extends Serializable, Service extends CrudService<ServiceE, Id>, DTO extends IdentifiableEntity<Id>> implements DTOCrudController<DTO, Id> {

    private Service crudService;
    private DTOMapper<DTO, ServiceE, Id> dtoToServiceEntityMapper;
    private DTOMapper<ServiceE, DTO, Id> serviceEntityToDTOMapper;

    public BasicDTOCrudController(Service crudService) {
        this.crudService = crudService;
    }

    //todo methoden einbauen die einfach nur die id returnen

    @SuppressWarnings("unchecked")
    public ResponseEntity<DTO> find(Id id) throws EntityMappingException, NoIdException, EntityNotFoundException {
        Optional<ServiceE> optionalEntity = crudService.findById(beforeFindEntity(id));
        //noinspection OptionalIsPresent
        if (optionalEntity.isPresent()) {
            return ok(getServiceEntityToDTOMapper().map(afterFindEntity(optionalEntity.get())));
        } else {
            throw new EntityNotFoundException();
        }
    }

    protected abstract DTOMapper<ServiceE, DTO, Id> provideServiceEntityToDTOMapper();

    //I like this better than in constructor
    protected abstract DTOMapper<DTO, ServiceE, Id> provideDTOToServiceEntityMapper();


    protected Id beforeFindEntity(Id id) {
        return id;
    }

    protected ServiceE afterFindEntity(ServiceE foundEntity) {
        return foundEntity;
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<DTO> create(DTO dto) throws EntityMappingException, BadEntityException {
        ServiceE serviceEntity = getDtoToServiceEntityMapper().map(dto);
        ServiceE savedServiceEntity = crudService.save(beforeCreateEntity(serviceEntity, dto));
        //no idea why casting is necessary here?
        return new ResponseEntity<DTO>((DTO) getServiceEntityToDTOMapper().map(afterCreateEntity(savedServiceEntity)), HttpStatus.OK);
    }


    protected ServiceE beforeCreateEntity(ServiceE entity, DTO dto) {
        return entity;
    }

    protected ServiceE afterCreateEntity(ServiceE entity) {
        return entity;
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<DTO> update(DTO dto) throws EntityMappingException, NoIdException, EntityNotFoundException, BadEntityException {
        ServiceE serviceEntity = getDtoToServiceEntityMapper().map(dto);
        ServiceE updatedServiceEntity = crudService.update(beforeUpdateEntity(serviceEntity));
        //no idea why casting is necessary here?
        return new ResponseEntity<DTO>((DTO) getServiceEntityToDTOMapper().map(afterUpdateEntity(updatedServiceEntity)), HttpStatus.OK);
    }

    protected ServiceE beforeUpdateEntity(ServiceE entity) {
        return entity;
    }

    ;

    protected ServiceE afterUpdateEntity(ServiceE entity) {
        return entity;
    }


    public ResponseEntity delete(Id id) throws NoIdException, EntityNotFoundException {
        crudService.deleteById(beforeDeleteEntity(id));
        afterDeleteEntity(id);
        return ResponseEntity.ok().build();
    }

    protected Id beforeDeleteEntity(Id id) {
        return id;
    }

    protected void afterDeleteEntity(Id id) {
    }


    private ResponseEntity<DTO> ok(DTO entity) {
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    public Service getCrudService() {
        return crudService;
    }

    public DTOMapper<DTO, ServiceE, Id> getDtoToServiceEntityMapper() {
        if (dtoToServiceEntityMapper == null) {
            this.dtoToServiceEntityMapper = provideDTOToServiceEntityMapper();
        }
        return dtoToServiceEntityMapper;
    }

    public DTOMapper<ServiceE, DTO, Id> getServiceEntityToDTOMapper() {
        if (serviceEntityToDTOMapper == null) {
            this.serviceEntityToDTOMapper = provideServiceEntityToDTOMapper();
        }
        return serviceEntityToDTOMapper;
    }
}
