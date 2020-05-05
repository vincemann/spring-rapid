package io.github.vincemann.springrapid.core.controller.rapid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.google.common.collect.Sets;
import io.github.vincemann.springrapid.core.advice.log.LogComponentInteractionAdvice;
import io.github.vincemann.springrapid.core.controller.dtoMapper.Delegating;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.CrudDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.UrlParamIdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.exception.IdFetchingException;
import io.github.vincemann.springrapid.core.controller.rapid.validationStrategy.ValidationStrategy;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.EndpointService;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Fully Functional CrudController.
 * <p>
 * Example-Request-URL's with {@link UrlParamIdFetchingStrategy}:
 * /entityName/httpMethod?entityIdName=id
 * <p>
 * /account/get?accountId=34
 * /account/get?accountId=44bedc08-8e71-11e9-bc42-526af7764f64
 *
 * @param <E>  Entity Type, of entity, which's crud operations are exposed, via endpoints,  by this Controller
 * @param <Id> Id Type of {@link E}
 *             <
 */
@Slf4j
@Getter
public abstract class RapidController
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                S extends CrudService<E, Id, ?>
                >
        implements ApplicationListener<ContextRefreshedEvent> {

    public static final String FIND_METHOD_NAME = "get";
    public static final String CREATE_METHOD_NAME = "create";
    public static final String DELETE_METHOD_NAME = "delete";
    public static final String UPDATE_METHOD_NAME = "update";
    public static final String FIND_ALL_METHOD_NAME = "getAll";

    @Setter
    private String findUrl;
    @Setter
    private String updateUrl;
    @Setter
    private String findAllUrl;
    @Setter
    private String deleteUrl;
    @Setter
    private String createUrl;
    private String baseUrl;
    private String entityNameInUrl;


    private EndpointService endpointService;
    private ObjectMapper jsonMapper;
    private IdFetchingStrategy<Id> idIdFetchingStrategy;
    private EndpointsExposureContext endpointsExposureContext;
    private S service;
    private S unsecuredService;
    private DtoMapper dtoMapper;
    private DtoMappingContext dtoMappingContext;
    private ValidationStrategy<Id> validationStrategy;

    @Setter
    private boolean serviceInteractionLogging = true;
    @Setter
    private String mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE;

    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @Autowired
    public RapidController(DtoMappingContext dtoMappingContext) {
        this.dtoMappingContext = dtoMappingContext;
        initUrls();
    }

    public RapidController() {
        initUrls();
    }

    /**
     * Override this with @Autowired @Qualifier("mySecuredService") if you are using a Security Proxy.
     * If you are using Rapid Acl Package override with @Secured.
     *
     * @param crudService
     */
    @Autowired
    @Lazy
    public void injectCrudService(S crudService) {
        this.service = crudService;
    }

    @Autowired
    @Lazy
    public void injectUnsecuredCrudService(S crudService) {
        this.unsecuredService = crudService;
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
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initRequestMapping();
    }

    @Autowired
    public void injectEndpointService(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @Autowired
    public void injectEndpointsExposureContext(EndpointsExposureContext endpointsExposureContext) {
        this.endpointsExposureContext = endpointsExposureContext;
    }

    @Autowired
    public void injectJsonMapper(ObjectMapper mapper) {
        this.jsonMapper = mapper;
    }


    @Autowired
    public void injectIdIdFetchingStrategy(IdFetchingStrategy<Id> idIdFetchingStrategy) {
        this.idIdFetchingStrategy = idIdFetchingStrategy;
    }

    private void initUrls() {
        this.entityNameInUrl = getEntityClass().getSimpleName().toLowerCase();
        this.baseUrl = "/" + entityNameInUrl + "/";
        this.findUrl = baseUrl + FIND_METHOD_NAME;
        this.findAllUrl = baseUrl + FIND_ALL_METHOD_NAME;
        this.updateUrl = baseUrl + UPDATE_METHOD_NAME;
        this.deleteUrl = baseUrl + DELETE_METHOD_NAME;
        this.createUrl = baseUrl + CREATE_METHOD_NAME;
    }

    protected void initRequestMapping() {
        try {
            if (endpointsExposureContext.isCreateEndpointExposed()) {
                //CREATE
                log.debug("Exposing create Endpoint for " + this.getClass().getSimpleName());
                getEndpointService().addMapping(getCreateRequestMappingInfo(),
                        this.getClass().getMethod("create", HttpServletRequest.class, HttpServletResponse.class), this);
            }

            if (endpointsExposureContext.isFindEndpointExposed()) {
                //GET
                log.debug("Exposing get Endpoint for " + this.getClass().getSimpleName());
                getEndpointService().addMapping(getFindRequestMappingInfo(),
                        this.getClass().getMethod("find", HttpServletRequest.class, HttpServletResponse.class), this);
            }

            if (endpointsExposureContext.isUpdateEndpointExposed()) {
                //UPDATE
                log.debug("Exposing update Endpoint for " + this.getClass().getSimpleName());
                getEndpointService().addMapping(getUpdateRequestMappingInfo(),
                        this.getClass().getMethod("update", HttpServletRequest.class, HttpServletResponse.class), this);
            }

            if (endpointsExposureContext.isDeleteEndpointExposed()) {
                //DELETE
                log.debug("Exposing delete Endpoint for " + this.getClass().getSimpleName());
                getEndpointService().addMapping(getDeleteRequestMappingInfo(),
                        this.getClass().getMethod("delete", HttpServletRequest.class, HttpServletResponse.class), this);
            }

            if (endpointsExposureContext.isFindAllEndpointExposed()) {
                //DELETE
                log.debug("Exposing findAll Endpoint for " + this.getClass().getSimpleName());
                getEndpointService().addMapping(getFindAllRequestMappingInfo(),
                        this.getClass().getMethod("findAll", HttpServletRequest.class, HttpServletResponse.class), this);
            }

        } catch (NoSuchMethodException e) {
            //should never happen
            throw new IllegalStateException(e);
        }
    }

    public RequestMappingInfo getFindRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findUrl)
                .methods(RequestMethod.GET)
                .produces(getMediaType())
                .build();
    }

    public RequestMappingInfo getDeleteRequestMappingInfo() {
        return RequestMappingInfo
                .paths(deleteUrl)
                .methods(RequestMethod.DELETE)
                .produces(getMediaType())
                .build();
    }

    public RequestMappingInfo getCreateRequestMappingInfo() {
        return RequestMappingInfo
                .paths(createUrl)
                .methods(RequestMethod.POST)
                .consumes(getMediaType())
                .produces(getMediaType())
                .build();
    }

    public RequestMappingInfo getUpdateRequestMappingInfo() {
        return RequestMappingInfo
                .paths(updateUrl)
                .methods(RequestMethod.PUT)
                .consumes(getMediaType())
                .produces(getMediaType())
                .build();
    }

    public RequestMappingInfo getFindAllRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findAllUrl)
                .methods(RequestMethod.GET)
                .produces(getMediaType())
                .build();
    }

    public ResponseEntity<String> findAll(HttpServletRequest request, HttpServletResponse response) throws DtoMappingException, DtoSerializingException {
        log.debug("FindAll request arriving at controller: " + request);
        beforeFindAll(request, response);
        logStateBeforeServiceCall("findAll");
        Set<E> foundEntities = serviceFindAll();
        logServiceResult("findAll", foundEntities);
        Collection<Object> dtos = new HashSet<>();
        for (E e : foundEntities) {
            dtos.add(dtoMapper.mapToDto(e,
                    findDtoClass(CrudDtoEndpoint.FIND_ALL, Direction.RESPONSE)));
        }
        afterFindAll(dtos, foundEntities, request, response);
        String json = null;
        try {
            json = jsonMapper.writeValueAsString(dtos);
        } catch (JsonProcessingException e) {
            throw new DtoSerializingException(e);
        }
        return ok(json);
    }


    public ResponseEntity<String> find(HttpServletRequest request, HttpServletResponse response) throws IdFetchingException, EntityNotFoundException, BadEntityException, DtoMappingException, DtoSerializingException {
        log.debug("Find request arriving at controller: " + request);
        Id id = idIdFetchingStrategy.fetchId(request);
        log.debug("id fetched from request: " + id);

        beforeFind(id, request, response);
        validationStrategy.validateId(id);
        log.debug("id successfully validated");
        logStateBeforeServiceCall("findById", id);
        Optional<E> optionalEntity = serviceFind(id);
        logServiceResult("findById", optionalEntity);
        EntityUtils.checkPresent(optionalEntity, id, getEntityClass());
        Object dto = dtoMapper.mapToDto(optionalEntity.get(),
                findDtoClass(CrudDtoEndpoint.FIND, Direction.RESPONSE));
        afterFind(id, dto, optionalEntity, request, response);
        try {
            return ok(jsonMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            throw new DtoSerializingException(e);
        }
    }

    public ResponseEntity<String> create(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, DtoMappingException, DtoSerializingException {
        log.debug("Create request arriving at controller: " + request);
        try {
            String json = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Class<?> dtoClass = findDtoClass(CrudDtoEndpoint.CREATE, Direction.REQUEST);
            Object dto = getJsonMapper().readValue(json, dtoClass);
            beforeCreate(dto, request, response);
            validationStrategy.validateDto(dto);
            log.debug("Dto successfully validated");
            //i expect that dto has the right dto type -> callers responsibility
            E entity = mapToEntity(dto);
            logStateBeforeServiceCall("save", entity);
            E savedEntity = serviceCreate(entity);
            logServiceResult("save", savedEntity);
            Object resultDto = dtoMapper.mapToDto(savedEntity,
                    findDtoClass(CrudDtoEndpoint.CREATE, Direction.RESPONSE));
            afterCreate(resultDto, entity, request, response);
            return ok(jsonMapper.writeValueAsString(resultDto));
        } catch (IOException e) {
            throw new DtoSerializingException(e);
        }
    }

    public ResponseEntity<String> update(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException, BadEntityException, DtoMappingException, DtoSerializingException, IdFetchingException, JsonPatchException {
        log.debug("Update request arriving at controller: " + request);
        try {
            String patchString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Id id = idIdFetchingStrategy.fetchId(request);
            Class<?> dtoClass = findDtoClass(CrudDtoEndpoint.UPDATE, Direction.REQUEST);
            beforeUpdate(dtoClass, id, patchString, request, response);

            Optional<E> saved = getUnsecuredService().findById(id);
            EntityUtils.checkPresent(saved, id, getEntityClass());
            Object patchDto = dtoMapper.mapToDto(saved.get(), dtoClass);
            patchDto = MapperUtils.applyPatch(patchDto, patchString);
            validationStrategy.validateDto(patchDto);
            E patch = dtoMapper.mapToEntity(patchDto, getEntityClass());
            E merged = merge(patch, saved.get(),dtoClass);
//            checkForInvalidUpdates(dtoClass, saved.get(), merged);
            logStateBeforeServiceCall("update", saved, patchString, merged);
            E updated = serviceUpdate(merged, true);
            logServiceResult("update", updated);
            //no idea why casting is necessary here?
            Class<?> resultDtoClass = findDtoClass(CrudDtoEndpoint.UPDATE, Direction.RESPONSE);
            Object resultDto = dtoMapper.mapToDto(updated, resultDtoClass);
            afterUpdate(resultDto, updated, request, response);
            return ok(jsonMapper.writeValueAsString(resultDto));
        } catch (IOException e) {
            throw new DtoSerializingException(e);
        }
    }

    protected E merge(E patch, E saved, Class<?> dtoClass) {
        Set<String> properties = Arrays.stream(ReflectionUtils.getDeclaredFields(dtoClass, true))
                .map(Field::getName)
                .collect(Collectors.toSet());
        for (String property : properties) {
            try {
                BeanUtilsBean.getInstance().copyProperty(saved, property, BeanUtilsBean.getInstance().getProperty(patch, property));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return saved;
    }

//    /**
//     * Only the properties defined in dtoClass can be changed.
//     */
//    protected void checkForInvalidUpdates(Class<?> dtoClass, E saved, E patched) throws BadEntityException {
//        try {
//            Set<String> allowedProperties = Sets.newHashSet(ReflectionUtils.getDeclaredFields(dtoClass, true)).stream().map(Field::getName).collect(Collectors.toSet());
//            Set<String> deniedProperties = Sets.newHashSet(ReflectionUtils.getDeclaredFields(getEntityClass(), true)).stream().map(Field::getName).collect(Collectors.toSet());
//            allowedProperties.forEach(deniedProperties::remove);
//            for (String deniedProperty : deniedProperties) {
//                String savedProperty = BeanUtilsBean.getInstance().getProperty(saved, deniedProperty);
//                String patchedProperty = BeanUtilsBean.getInstance().getProperty(patched, deniedProperty);
//                if (!savedProperty.equals(patchedProperty)) {
//                    throw new BadEntityException("Property: " + deniedProperty + " must not be updated by current user.");
//                }
//            }
//        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//    }


    public ResponseEntity<?> delete(HttpServletRequest request, HttpServletResponse response) throws IdFetchingException, BadEntityException, EntityNotFoundException, ConstraintViolationException {
        log.debug("Delete request arriving at controller: " + request);
        Id id = idIdFetchingStrategy.fetchId(request);
        log.debug("id fetched from request: " + id);
        beforeDelete(id, request, response);
        validationStrategy.validateId(id);
        log.debug("id successfully validated");
        logStateBeforeServiceCall("delete", id);
        serviceDelete(id);
        afterDelete(id, request, response);
        return ok();
    }

    public Class<?> findDtoClass(String endpoint, Direction direction) {
        DtoMappingInfo endpointInfo = createEndpointInfo(endpoint, direction);
        return getDtoMappingContext().find(endpointInfo);
    }

    protected DtoMappingInfo createEndpointInfo(String endpoint, Direction direction) {
        return DtoMappingInfo.builder()
                .authorities(AuthorityUtil.getAuthorities())
                .direction(direction)
                .endpoint(endpoint)
                .build();
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

    private E mapToEntity(Object dto) throws DtoMappingException {
        return dtoMapper.mapToEntity(dto, entityClass);
    }


    protected ResponseEntity<String> ok(String jsonDto) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(getMediaType()))
                .body(jsonDto);
    }

    protected ResponseEntity<?> ok() {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(getMediaType()))
                .build();
    }

    // overrideable
    protected E serviceUpdate(E update, boolean full) throws BadEntityException, EntityNotFoundException {
        return service.update(update, full);
    }

    protected E serviceCreate(E entity) throws BadEntityException {
        return service.save(entity);
    }

    protected void serviceDelete(Id id) throws BadEntityException, EntityNotFoundException {
        service.deleteById(id);
    }

    protected Set<E> serviceFindAll() {
        return service.findAll();
    }

    protected Optional<E> serviceFind(Id id) throws BadEntityException {
        return service.findById(id);
    }


    //callbacks
    public void beforeCreate(Object dto, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void beforeUpdate(Class<?> dtoClass, Id id, String patchString, HttpServletRequest request, HttpServletResponse response) {
    }

    public void beforeDelete(Id id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void beforeFind(Id id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void beforeFindAll(HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    //callbacks
    public void afterCreate(Object dto, E created, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterUpdate(Object dto, E updated, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterDelete(Id id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterFind(Id id, Object dto, Optional<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterFindAll(Collection<Object> dtos, Set<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }


}
