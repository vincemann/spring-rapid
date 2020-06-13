package com.github.vincemann.springrapid.core.controller.rapid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.vincemann.springrapid.core.advice.log.LogComponentInteractionAdvice;
import com.github.vincemann.springrapid.core.controller.dtoMapper.DelegatingDtoMapper;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoEndpoint;
import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.IdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.UrlParamIdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.exception.IdFetchingException;
import com.github.vincemann.springrapid.core.controller.rapid.mergeUpdate.MergeUpdateStrategy;
import com.github.vincemann.springrapid.core.controller.rapid.validationStrategy.ValidationStrategy;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.EndpointService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.AuthorityUtil;
import com.github.vincemann.springrapid.core.util.JpaUtils;
import com.github.vincemann.springrapid.core.util.MapperUtils;
import com.github.vincemann.springrapid.core.util.RapidUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
 */
@Slf4j
@Getter
public abstract class RapidController
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                S extends CrudService<E, Id, ?>
                >
        implements ApplicationListener<ContextRefreshedEvent>, InitializingBean {

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
    private DelegatingDtoMapper dtoMapper;
    private DelegatingOwnerLocator ownerLocator;
    private ExtendableDtoClassLocator dtoClassLocator;
    @Setter
    private DtoMappingContext dtoMappingContext;
    private ValidationStrategy<Id> validationStrategy;
    private MergeUpdateStrategy<E> mergeUpdateStrategy;

    @Setter
    private boolean serviceInteractionLogging = true;
    @Setter
    private String mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE;

    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @Autowired
    public RapidController() {
        this.dtoMappingContext = provideDtoMappingContext();
        initUrls();
    }

    protected abstract DtoMappingContext provideDtoMappingContext();

    /**
     * Override this method if you want to register {@link LocalDtoClassLocator}
     * @param locator
     */
    protected void configureDtoClassLocator(ExtendableDtoClassLocator locator){

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dtoClassLocator.setContext(dtoMappingContext);
        configureDtoClassLocator(dtoClassLocator);
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

    @Autowired
    public void injectMergeUpdateStrategy(MergeUpdateStrategy<E> mergeUpdateStrategy) {
        this.mergeUpdateStrategy = mergeUpdateStrategy;
    }

    @Autowired
    public void injectOwnerLocator(DelegatingOwnerLocator ownerLocator) {
        this.ownerLocator = ownerLocator;
    }

    @Autowired
    public void injectValidationStrategy(ValidationStrategy<Id> validationStrategy) {
        this.validationStrategy = validationStrategy;
    }

    @Autowired
    public void injectDtoClassLocator(ExtendableDtoClassLocator dtoClassLocator) {
        this.dtoClassLocator = dtoClassLocator;
    }

    @Autowired
    public void injectDtoMapper(DelegatingDtoMapper dtoMapper) {
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

    public ResponseEntity<String> findAll(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        try {
            log.debug("FindAll request arriving at controller: " + request);
            beforeFindAll(request, response);
            logStateBeforeServiceCall("findAll");
            Set<E> foundEntities = serviceFindAll();
            logServiceResult("findAll", foundEntities);
            Collection<Object> dtos = new HashSet<>();
            for (E e : foundEntities) {
                dtos.add(dtoMapper.mapToDto(e,
                        createDtoClass(RapidDtoEndpoint.FIND_ALL, Direction.RESPONSE, null)));
            }
            afterFindAll(dtos, foundEntities, request, response);
            String json = jsonMapper.writeValueAsString(dtos);
            return ok(json);
        } catch (BadEntityException e) {
            throw new RuntimeException(e);
        }

    }


    public ResponseEntity<String> find(HttpServletRequest request, HttpServletResponse response) throws IdFetchingException, EntityNotFoundException, BadEntityException, JsonProcessingException {
        log.debug("Find request arriving at controller: " + request);
        Id id = idIdFetchingStrategy.fetchId(request);
        log.debug("id fetched from request: " + id);

        beforeFind(id, request, response);
        validationStrategy.validateId(id);
        log.debug("id successfully validated");
        logStateBeforeServiceCall("findById", id);
        Optional<E> optionalEntity = serviceFind(id);
        logServiceResult("findById", optionalEntity);
        RapidUtils.checkPresent(optionalEntity, id, getEntityClass());
        E found = optionalEntity.get();
        Object dto = dtoMapper.mapToDto(
                found,
                createDtoClass(RapidDtoEndpoint.FIND, Direction.RESPONSE,found)
        );
        afterFind(id, dto, optionalEntity, request, response);
        return ok(jsonMapper.writeValueAsString(dto));

    }

    public ResponseEntity<String> create(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IOException {
        log.debug("Create request arriving at controller: " + request);
        String json = readBody(request);
        Class<?> dtoClass = createDtoClass(RapidDtoEndpoint.CREATE, Direction.REQUEST, null);
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
                createDtoClass(RapidDtoEndpoint.CREATE, Direction.RESPONSE, null));
        afterCreate(resultDto, entity, request, response);
        return ok(jsonMapper.writeValueAsString(resultDto));
    }

    public ResponseEntity<String> update(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException, IdFetchingException, JsonPatchException, IOException {
        log.debug("Update request arriving at controller: " + request);
        String patchString = readBody(request);
        log.debug("patchString: " + patchString);
        Id id = idIdFetchingStrategy.fetchId(request);
        Optional<E> savedOptional = getUnsecuredService().findById(id);
        RapidUtils.checkPresent(savedOptional, id, getEntityClass());
        E saved = savedOptional.get();
        Class<?> dtoClass = createDtoClass(RapidDtoEndpoint.UPDATE, Direction.REQUEST, saved);
        beforeUpdate(dtoClass, id, patchString, request, response);

        Object patchDto = dtoMapper.mapToDto(saved, dtoClass);
        patchDto = MapperUtils.applyPatch(patchDto, patchString);
        log.debug("finished patchDto: " + patchDto);
        validationStrategy.validateDto(patchDto);
        E patchEntity = dtoMapper.mapToEntity(patchDto, getEntityClass());
        log.debug("finished patchEntity: " + patchEntity);
        E merged = mergeUpdateStrategy.merge(patchEntity, JpaUtils.detach(saved), dtoClass);
        log.debug("merged Entity as input for service: ");
        logStateBeforeServiceCall("update", merged, true);
        E updated = serviceUpdate(merged, true);
        logServiceResult("update", updated);
        //no idea why casting is necessary here?
        Class<?> resultDtoClass = createDtoClass(RapidDtoEndpoint.UPDATE, Direction.RESPONSE, saved);
        Object resultDto = dtoMapper.mapToDto(updated, resultDtoClass);
        afterUpdate(resultDto, updated, request, response);
        return ok(jsonMapper.writeValueAsString(resultDto));
    }


    protected String readBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    }


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

    public Class<?> createDtoClass(String endpoint, Direction direction, E entity) {
        DtoMappingInfo endpointInfo = createEndpointInfo(endpoint, direction, entity);
        log.debug("DtoMappingInfo of current Request: " +endpointInfo);
        Class<?> dtoClazz = dtoClassLocator.find(endpointInfo);
        log.debug("Found DtoClass: " + dtoClazz);
        return dtoClazz;
    }

    protected DtoMappingInfo createEndpointInfo(String endpoint, Direction direction, E entity) {
        DtoMappingInfo.Principal principal = currentPrincipal(entity);
        return DtoMappingInfo.builder()
                .authorities(AuthorityUtil.getAuthorities())
                .direction(direction)
                .principal(principal)
                .endpoint(endpoint)
                .build();
    }

    protected DtoMappingInfo.Principal currentPrincipal(E entity){
        DtoMappingInfo.Principal principal = DtoMappingInfo.Principal.ALL;
        if (entity!=null) {
            Optional<String> authenticated = AuthorityUtil.getAuthenticatedName();
            Optional<String> queried = ownerLocator.find(entity);
            if (queried.isPresent() && authenticated.isPresent()) {
                principal = queried.equals(authenticated)
                        ? DtoMappingInfo.Principal.OWN
                        : DtoMappingInfo.Principal.FOREIGN;
            }
        }
        return principal;
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

    private E mapToEntity(Object dto) throws BadEntityException, EntityNotFoundException {
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
