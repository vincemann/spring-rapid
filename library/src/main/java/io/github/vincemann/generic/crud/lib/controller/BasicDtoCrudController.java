package io.github.vincemann.generic.crud.lib.controller;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
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
 * @param <ServiceE> Service Entity Type, of entity, which curd enpoints are exposed by this Controller
 * @param <Service>  Service Type of {@link ServiceE}
 * @param <Dto>      Dto Type corresponding to {@link ServiceE}
 * @param <Id>       Id Type of {@link ServiceE}
 */

public abstract class BasicDtoCrudController
        <
                ServiceE extends IdentifiableEntity<Id>,
                Dto extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<ServiceE,Id>,
                Service extends CrudService<ServiceE, Id,R>
        >
            implements DtoCrudController<Dto, Id> {

    @Getter
    @Setter
    private CrudService<ServiceE,Id,R> crudService;


    @Getter
    private DtoMapper dtoMapper;
    @SuppressWarnings("unchecked")
    @Getter
    private Class<ServiceE> serviceEntityClass = (Class<ServiceE>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    @SuppressWarnings("unchecked")
    @Getter
    private Class<Dto> dtoClass = (Class<Dto>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    private List<Plugin<? super ServiceE,? super Id>> plugins = new ArrayList<>();


    public BasicDtoCrudController(Plugin<? super ServiceE,? super Id>... controllerAwarePlugins) {
        List<Plugin<? super ServiceE, ? super Id>> plugins = Arrays.asList(controllerAwarePlugins);
        plugins.forEach(plugin -> {
            if(plugin instanceof ControllerAwarePlugin) {
                ((ControllerAwarePlugin<? super ServiceE, ? super Id>) plugin).setController(this);
            }
            this.plugins.add(plugin);
        });
    }

    public BasicDtoCrudController() {
    }

    public void setCrudService(CrudService<ServiceE, Id, R> crudService) {
        this.crudService = crudService;
    }

    @Autowired
    public void injectCrudService(CrudService<ServiceE,Id,R> crudService) {
        this.crudService = crudService;
    }

    @Autowired
    public void injectDtoMapper(DtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }


    //todo implement methods that just return id and not whole dtos (create, update)

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<Dto> find(Id id) throws NoIdException, EntityNotFoundException, EntityMappingException {
        beforeFindEntity(id);
        Optional<ServiceE> optionalEntity = crudService.findById(id);
        //noinspection OptionalIsPresent
        if (optionalEntity.isPresent()) {
            afterFindEntity(optionalEntity.get());
            return ok(getDtoMapper().mapServiceEntityToDto(optionalEntity.get(),dtoClass));
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public ResponseEntity<Collection<Dto>> findAll() throws EntityMappingException {
        beforeFindAllEntities();
        Set<ServiceE> all = crudService.findAll();
        afterFindAllEntities(all);
        Set<Dto> dtos = new HashSet<>();
        for (ServiceE serviceE : all) {
            dtos.add(getDtoMapper().mapServiceEntityToDto(serviceE,dtoClass));
        }
        return ok(dtos);
    }

    protected void beforeFindAllEntities(){
        plugins.forEach(Plugin::beforeFindAllEntities);
    }

    protected void afterFindAllEntities(Set<ServiceE> all){
        plugins.forEach(plugin -> plugin.afterFindAllEntities(all));
    }


    protected void beforeFindEntity(Id id) {
        plugins.forEach(plugin -> plugin.beforeFindEntity(id));
    }

    protected void afterFindEntity(ServiceE foundEntity) {
        plugins.forEach(plugin -> plugin.afterFindEntity(foundEntity));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<Dto> create(Dto dto) throws BadEntityException, EntityMappingException {
        ServiceE serviceEntity = getDtoMapper().mapDtoToServiceEntity(dto,serviceEntityClass);
        beforeCreateEntity(serviceEntity);
        ServiceE savedServiceEntity = crudService.save(serviceEntity);
        afterCreateEntity(savedServiceEntity);
        return new ResponseEntity(getDtoMapper().mapServiceEntityToDto(savedServiceEntity,dtoClass), HttpStatus.OK);
    }


    protected void beforeCreateEntity(ServiceE entity) {
        plugins.forEach(plugin -> plugin.beforeCreateEntity(entity));
    }

    protected void afterCreateEntity(ServiceE entity) {
        plugins.forEach(plugin -> plugin.afterCreateEntity(entity));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<Dto> update(Dto dto) throws BadEntityException, EntityMappingException, NoIdException, EntityNotFoundException {
        ServiceE serviceEntity = getDtoMapper().mapDtoToServiceEntity(dto,serviceEntityClass);
        beforeUpdateEntity(serviceEntity);
        ServiceE updatedServiceEntity = crudService.update(serviceEntity);
        //no idea why casting is necessary here?
        afterUpdateEntity(updatedServiceEntity);
        return new ResponseEntity(getDtoMapper().mapServiceEntityToDto(updatedServiceEntity,dtoClass), HttpStatus.OK);
    }

    protected void beforeUpdateEntity(ServiceE entity) {
        plugins.forEach(plugin -> plugin.beforeUpdateEntity(entity));
    }

    protected void afterUpdateEntity(ServiceE entity) {
        plugins.forEach(plugin -> plugin.afterUpdateEntity(entity));
    }

    @Override
    public ResponseEntity delete(Id id) throws NoIdException, EntityNotFoundException {
        beforeDeleteEntity(id);
        crudService.deleteById(id);
        afterDeleteEntity(id);
        return ResponseEntity.ok().build();
    }

    protected void beforeDeleteEntity(Id id) {
        plugins.forEach(plugin -> plugin.beforeDeleteEntity(id));
    }

    protected void afterDeleteEntity(Id id) {
        plugins.forEach(plugin -> plugin.afterDeleteEntity(id));
    }

    private ResponseEntity<Collection<Dto>> ok(Collection<Dto> dtoCollection){
        return new ResponseEntity<>(dtoCollection,HttpStatus.OK);
    }

    private ResponseEntity<Dto> ok(Dto entity) {
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    public interface Plugin<ServiceE extends IdentifiableEntity<Id>,Id extends Serializable> {

        public void beforeFindEntity(Id id);

        public void afterFindEntity(ServiceE foundEntity);

        public void beforeCreateEntity(ServiceE entity);

        public void afterCreateEntity(ServiceE entity);

        public void beforeUpdateEntity(ServiceE entity);

        public void afterUpdateEntity(ServiceE entity);

        public void beforeDeleteEntity(Id id);

        public void afterDeleteEntity(Id id);

        public void beforeFindAllEntities();

        public void afterFindAllEntities(Set<? extends ServiceE> all);
    }


    @Setter
    @Getter
    public static abstract class ControllerAwarePlugin<ServiceE extends IdentifiableEntity<Id>,Id extends Serializable> implements BasicDtoCrudController.Plugin<ServiceE,Id> {
        private BasicDtoCrudController controller;
    }


    public List<Plugin<? super ServiceE, ? super Id>> getBasicPlugins() {
        return plugins;
    }

    public CrudService<ServiceE, Id, R> getCrudService() {
        return crudService;
    }

    public Service getCastedCrudService(){
        return (Service) crudService;
    }
}
