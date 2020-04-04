package io.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vincemann.springrapid.core.advice.log.LogComponentInteractionAdvice;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.CrudDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.dtoMapper.exception.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.springAdapter.DtoSerializingException;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.service.exception.NoIdException;
import io.github.vincemann.springrapid.core.util.AuthorityUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

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
public abstract class JsonDtoCrudController
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable
        >
            implements DtoCrudController<Id> {

    //todo merge into spring adapter -> wo ist der sinn hier zu trennen?

    private CrudService<E,Id,? extends CrudRepository<E,Id>> crudService;
    private DtoMapper dtoMapper;
    private DtoMappingContext dtoMappingContext;
    @Setter
    private ObjectMapper jsonMapper;
    private boolean serviceInteractionLogging= true;

    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];


    public JsonDtoCrudController(DtoMappingContext dtoMappingContext) {
        this.dtoMappingContext = dtoMappingContext;
    }

    @Autowired
    public void injectCrudService(CrudService<E,Id,? extends CrudRepository<E,Id>> crudService) {
        this.crudService = crudService;
    }

    public void setDtoMappingContext(DtoMappingContext dtoMappingContext) {
        this.dtoMappingContext = dtoMappingContext;
    }

    @Autowired
    public void injectJsonMapper(ObjectMapper mapper) {
        this.jsonMapper = mapper;
    }

    @Autowired
    public void injectDtoMapper(DtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<String> find(Id id) throws NoIdException, EntityNotFoundException, DtoMappingException, DtoSerializingException {
        try {
            logStateBeforeServiceCall("findById",id);
            Optional<E> optionalEntity = crudService.findById(id);
            logServiceResult("findById",optionalEntity);
            if (optionalEntity.isPresent()) {
                IdentifiableEntity<?> dto = dtoMapper.mapToDto(optionalEntity.get(),
                        findDtoClass(CrudDtoEndpoint.FIND, Direction.RESPONSE));
                return ok(jsonMapper.writeValueAsString(dto));
            } else {
                throw new EntityNotFoundException();
            }
        }catch (JsonProcessingException e){
            throw new DtoSerializingException(e);
        }
    }

    public Class<? extends IdentifiableEntity> findDtoClass(String endpoint, Direction direction){
        DtoMappingInfo endpointInfo = createEndpointInfo(endpoint, direction);
        Class<? extends IdentifiableEntity> dtoClass = getDtoMappingContext().find(endpointInfo);
        return dtoClass;
    }

    protected DtoMappingInfo createEndpointInfo(String endpoint, Direction direction){
        return DtoMappingInfo.builder()
                .authorities(AuthorityUtil.getAuthorities())
                .direction(direction)
                .endpoint(endpoint)
                .build();
    }

    @Override
    public ResponseEntity<String> findAll() throws DtoMappingException, DtoSerializingException {
        try {
            logStateBeforeServiceCall("findAll");
            Set<E> all = crudService.findAll();
            logServiceResult("findAll",all);
            Collection<IdentifiableEntity<Id>> dtos = new HashSet<>();
            for (E e : all) {
                dtos.add(dtoMapper.mapToDto(e,
                        findDtoClass(CrudDtoEndpoint.FIND_ALL,Direction.RESPONSE)));
            }
            log.debug("Input for JsonMapper (Dto): " + dtos);
            String json = jsonMapper.writeValueAsString(dtos);
            return ok(json);
        } catch (JsonProcessingException e) {
            throw new DtoSerializingException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<String> create(IdentifiableEntity<Id> dto) throws BadEntityException, DtoMappingException, DtoSerializingException {
        try {
            //i expect that dto has the right dto type -> callers responsibility
            E entity = mapToEntity(dto);
            logStateBeforeServiceCall("save",entity);
            E savedEntity = crudService.save(entity);
            logServiceResult("save",savedEntity);
            IdentifiableEntity<?> resultDto = dtoMapper.mapToDto(savedEntity,
                    findDtoClass(CrudDtoEndpoint.CREATE,Direction.RESPONSE));
            return new ResponseEntity<>(
                    jsonMapper.writeValueAsString(resultDto),
                    HttpStatus.OK);
        }catch (JsonProcessingException e){
            throw new DtoSerializingException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<String> update(IdentifiableEntity<Id> dto, boolean full) throws BadEntityException, DtoMappingException, NoIdException, EntityNotFoundException, DtoSerializingException {
        try {
            //i expect that dto has the right dto type -> callers responsibility
            E entity = mapToEntity(dto);
            logStateBeforeServiceCall("update",entity,full);
            E updatedEntity = crudService.update(entity,full);
            logServiceResult("update",updatedEntity);
            //no idea why casting is necessary here?
            Class<? extends IdentifiableEntity> dtoClass;
            if(full){
                dtoClass = findDtoClass(CrudDtoEndpoint.FULL_UPDATE,Direction.RESPONSE);
            }else {
                dtoClass = findDtoClass(CrudDtoEndpoint.PARTIAL_UPDATE,Direction.RESPONSE);
            }
            IdentifiableEntity<?> resultDto = dtoMapper.mapToDto(updatedEntity,dtoClass);
            return new ResponseEntity<>(
                    jsonMapper.writeValueAsString(resultDto),
                    HttpStatus.OK);
        }catch (JsonProcessingException e){
            throw new DtoSerializingException(e);
        }
    }

    protected void logStateBeforeServiceCall(String methodName, Object... args ){
        if(serviceInteractionLogging) {
            LogComponentInteractionAdvice.logArgs(methodName, args);
            log.info("SecurityContexts Authentication right before service call: " + SecurityContextHolder.getContext().getAuthentication());
        }
    }

    protected void logServiceResult(String methodName, Object result){
        if(serviceInteractionLogging)
            LogComponentInteractionAdvice.logResult(methodName,result);
    }


    @Override
    public ResponseEntity<String> delete(Id id) throws NoIdException, EntityNotFoundException {
        logStateBeforeServiceCall("delete",id);
        crudService.deleteById(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).build();
    }


    private E mapToEntity(IdentifiableEntity<Id> dto) throws DtoMappingException {
        return dtoMapper.mapToEntity(dto,entityClass);
    }


    protected ResponseEntity<String> ok(String jsonDto) {
        return new ResponseEntity<>(jsonDto, HttpStatus.OK);
    }

    public <S extends CrudService<E, Id,? extends CrudRepository<E,Id>>> S getCastedCrudService(){
        return (S) crudService;
    }

}