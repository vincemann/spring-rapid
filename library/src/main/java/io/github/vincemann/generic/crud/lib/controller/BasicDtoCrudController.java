package io.github.vincemann.generic.crud.lib.controller;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
import lombok.Getter;
import lombok.Setter;
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
@Getter
public abstract class BasicDtoCrudController<ServiceE extends IdentifiableEntity<Id>,Dto extends IdentifiableEntity<Id>,  Id extends Serializable, Service extends CrudService<ServiceE, Id>>
        implements DtoCrudController<Dto, Id> {

    private Service crudService;
    private DtoMapper dtoMapper;
    @SuppressWarnings("unchecked")
    private Class<ServiceE> serviceEntityClass = (Class<ServiceE>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    @SuppressWarnings("unchecked")
    private Class<Dto> dtoClass = (Class<Dto>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    private List<Plugin<? super ServiceE,? super Id>> basicCrudControllerPlugins = new ArrayList<>();

    public BasicDtoCrudController(Service crudService,
                                  DtoMapper dtoMapper,
                                  AbstractPlugin<? super ServiceE,? super Id>... crudControllerPlugins
    ) {
        List<AbstractPlugin<? super ServiceE, ? super Id>> plugins = Arrays.asList(crudControllerPlugins);
        plugins.forEach(plugin -> plugin.setController(this));
        this.basicCrudControllerPlugins.addAll(plugins);
        this.crudService = crudService;
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
        basicCrudControllerPlugins.forEach(Plugin::beforeFindAllEntities);
    }

    protected void afterFindAllEntities(Set<ServiceE> all){
        basicCrudControllerPlugins.forEach(plugin -> plugin.afterFindAllEntities(all));
    }


    protected void beforeFindEntity(Id id) {
        basicCrudControllerPlugins.forEach(plugin -> plugin.beforeFindEntity(id));
    }

    protected void afterFindEntity(ServiceE foundEntity) {
        basicCrudControllerPlugins.forEach(plugin -> plugin.afterFindEntity(foundEntity));
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
        basicCrudControllerPlugins.forEach(plugin -> plugin.beforeCreateEntity(entity));
    }

    protected void afterCreateEntity(ServiceE entity) {
        basicCrudControllerPlugins.forEach(plugin -> plugin.afterCreateEntity(entity));
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
        basicCrudControllerPlugins.forEach(plugin -> plugin.beforeUpdateEntity(entity));
    }

    protected void afterUpdateEntity(ServiceE entity) {
        basicCrudControllerPlugins.forEach(plugin -> plugin.afterUpdateEntity(entity));
    }

    @Override
    public ResponseEntity delete(Id id) throws NoIdException, EntityNotFoundException {
        beforeDeleteEntity(id);
        crudService.deleteById(id);
        afterDeleteEntity(id);
        return ResponseEntity.ok().build();
    }

    protected void beforeDeleteEntity(Id id) {
        basicCrudControllerPlugins.forEach(plugin -> plugin.beforeDeleteEntity(id));
    }

    protected void afterDeleteEntity(Id id) {
        basicCrudControllerPlugins.forEach(plugin -> plugin.afterDeleteEntity(id));
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
    public static abstract class AbstractPlugin<ServiceE extends IdentifiableEntity<Id>,Id extends Serializable> implements BasicDtoCrudController.Plugin<ServiceE,Id> {
        private BasicDtoCrudController controller;
    }


}
