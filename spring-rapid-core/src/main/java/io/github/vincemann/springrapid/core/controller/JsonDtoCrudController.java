package io.github.vincemann.springrapid.core.controller;

import io.github.vincemann.springrapid.core.advice.log.LogComponentInteractionAdvice;
import io.github.vincemann.springrapid.core.controller.dtoMapper.Delegating;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DelegatingDtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.CrudDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo;
import io.github.vincemann.springrapid.core.controller.rapid.DtoSerializingException;
import io.github.vincemann.springrapid.core.controller.rapid.validationStrategy.ValidationStrategy;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.util.AuthorityUtil;
import io.github.vincemann.springrapid.core.util.EntityUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


/**
 * Impl of {@link DtoCrudController} that handles the following:
 * Mapping of Entity to Dto and vice versa with {@link DtoMapper}.
 * Validation of the Dto and {@link Id} with {@link ValidationStrategy}
 * Interaction with {@link CrudService}.
 * Supply hook Methods.
 *
 * @param <E>  Entity Type, of entity, which's crud endpoints are exposed by this Controller
 * @param <Id> Id Type of {@link E}
 */
@Slf4j
@Getter
@Setter
public abstract class JsonDtoCrudController
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                S extends CrudService<E,Id,?>
                >
        implements DtoCrudController<Id> {

    //todo merge into spring adapter -> wo ist der sinn hier zu trennen?

    private S crudService;
    private DtoMapper dtoMapper;
    private DtoMappingContext dtoMappingContext;
    private ValidationStrategy<Id> validationStrategy;
    private boolean serviceInteractionLogging = true;

    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];


    public JsonDtoCrudController(DtoMappingContext dtoMappingContext) {
        this.dtoMappingContext = dtoMappingContext;
    }

    @Autowired
    public void injectCrudService(S crudService) {
        this.crudService = crudService;
    }

    public void setDtoMappingContext(DtoMappingContext dtoMappingContext) {
        this.dtoMappingContext = dtoMappingContext;
    }

    @Autowired
    public void injectValidationStrategy(ValidationStrategy<Id> validationStrategy) {
        this.validationStrategy = validationStrategy;
    }


    @Delegating
    @Autowired
    public void injectDtoMapper(DtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IdentifiableEntity<Id> find(Id id) throws BadEntityException, EntityNotFoundException, DtoMappingException, DtoSerializingException {
        validationStrategy.validateId(id);
        log.debug("id successfully validated");
        logStateBeforeServiceCall("findById", id);
        Optional<E> optionalEntity = crudService.findById(id);
        logServiceResult("findById", optionalEntity);
        EntityUtils.checkPresent(optionalEntity, id, getEntityClass());
        IdentifiableEntity<Id> dto = dtoMapper.mapToDto(optionalEntity.get(),
                findDtoClass(CrudDtoEndpoint.FIND, Direction.RESPONSE));
        return dto;
    }

    public Class<? extends IdentifiableEntity> findDtoClass(String endpoint, Direction direction) {
        DtoMappingInfo endpointInfo = createEndpointInfo(endpoint, direction);
        Class<? extends IdentifiableEntity> dtoClass = getDtoMappingContext().find(endpointInfo);
        return dtoClass;
    }

    protected DtoMappingInfo createEndpointInfo(String endpoint, Direction direction) {
        return DtoMappingInfo.builder()
                .authorities(AuthorityUtil.getAuthorities())
                .direction(direction)
                .endpoint(endpoint)
                .build();
    }

    @Override
    public Collection<IdentifiableEntity<Id>> findAll() throws DtoMappingException, DtoSerializingException {
        logStateBeforeServiceCall("findAll");
        Set<E> all = crudService.findAll();
        logServiceResult("findAll", all);
        Collection<IdentifiableEntity<Id>> dtos = new HashSet<>();
        for (E e : all) {
            dtos.add(dtoMapper.mapToDto(e,
                    findDtoClass(CrudDtoEndpoint.FIND_ALL, Direction.RESPONSE)));
        }
        return dtos;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IdentifiableEntity<Id> create(IdentifiableEntity<Id> dto) throws BadEntityException, DtoMappingException, DtoSerializingException {
        validationStrategy.validateDto(dto);
        log.debug("Dto successfully validated");
        //i expect that dto has the right dto type -> callers responsibility
        E entity = mapToEntity(dto);
        logStateBeforeServiceCall("save", entity);
        E savedEntity = crudService.save(entity);
        logServiceResult("save", savedEntity);
        IdentifiableEntity<Id> resultDto = dtoMapper.mapToDto(savedEntity,
                findDtoClass(CrudDtoEndpoint.CREATE, Direction.RESPONSE));
        return resultDto;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IdentifiableEntity<Id> update(IdentifiableEntity<Id> dto, boolean full) throws BadEntityException, DtoMappingException, BadEntityException, EntityNotFoundException, DtoSerializingException {
        validationStrategy.validateDto(dto);
        log.debug("Dto successfully validated");
        //i expect that dto has the right dto type -> callers responsibility
        E entity = mapToEntity(dto);
        logStateBeforeServiceCall("update", entity, full);
        E updatedEntity = crudService.update(entity, full);
        logServiceResult("update", updatedEntity);
        //no idea why casting is necessary here?
        Class<? extends IdentifiableEntity> dtoClass;
        if (full) {
            dtoClass = findDtoClass(CrudDtoEndpoint.FULL_UPDATE, Direction.RESPONSE);
        } else {
            dtoClass = findDtoClass(CrudDtoEndpoint.PARTIAL_UPDATE, Direction.RESPONSE);
        }
        return dtoMapper.mapToDto(updatedEntity, dtoClass);
    }

    protected void logStateBeforeServiceCall(String methodName, Object... args) {
        if (serviceInteractionLogging) {
            LogComponentInteractionAdvice.logArgs(methodName, args);
            log.info("SecurityContexts Authentication right before service call: " + SecurityContextHolder.getContext().getAuthentication());
        }
    }

    protected void logServiceResult(String methodName, Object result) {
        if (serviceInteractionLogging)
            LogComponentInteractionAdvice.logResult(methodName, result);
    }


    @Override
    public void delete(Id id) throws BadEntityException, EntityNotFoundException {
        validationStrategy.validateId(id);
        log.debug("id successfully validated");
        logStateBeforeServiceCall("delete", id);
        crudService.deleteById(id);
    }


    private E mapToEntity(IdentifiableEntity<Id> dto) throws DtoMappingException {
        return dtoMapper.mapToEntity(dto, entityClass);
    }


    protected ResponseEntity<String> ok(String jsonDto) {
        return new ResponseEntity<>(jsonDto, HttpStatus.OK);
    }

}