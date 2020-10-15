package com.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.vincemann.springrapid.core.RapidCoreProperties;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DelegatingDtoMapper;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;
import com.github.vincemann.springrapid.core.controller.idFetchingStrategy.IdFetchingException;
import com.github.vincemann.springrapid.core.controller.idFetchingStrategy.IdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.idFetchingStrategy.UrlParamIdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.mergeUpdate.MergeUpdateStrategy;
import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.controller.validationStrategy.ValidationStrategy;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.EndpointService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.JpaUtils;
import com.github.vincemann.springrapid.core.util.JsonUtils;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
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
 * @param <E>  Entity Type, of entity, who's crud operations are exposed, via endpoints,  by this Controller
 * @param <ID> Id Type of {@link E}
 */
@Slf4j
@Getter
public abstract class GenericCrudController
        <
                E extends IdentifiableEntity<ID>,
                ID extends Serializable,
                S extends CrudService<E, ID>,

                //internal generic params
                EndpointInfo extends CrudEndpointInfo,
                DTOMappingContextBuilder extends DtoMappingContextBuilder
                >
        implements
        ApplicationListener<ContextRefreshedEvent>,
        InitializingBean {


    //              DEPENDENCIES


    private RapidCoreProperties coreProperties;
    private EndpointService endpointService;
    private ObjectMapper jsonMapper;
    private IdFetchingStrategy<ID> idIdFetchingStrategy;
    private EndpointInfo endpointInfo;
    private S service;
    private DelegatingDtoMapper dtoMapper;
    private DelegatingOwnerLocator ownerLocator;
    private DelegatingDtoClassLocator dtoClassLocator;
    @Setter
    private DtoMappingContext dtoMappingContext;
    private DTOMappingContextBuilder dtoMappingContextBuilder;
    private ValidationStrategy<ID> validationStrategy;
    private MergeUpdateStrategy mergeUpdateStrategy;
    private Class<E> entityClass;


    //              CONTROLLER METHODS


    public ResponseEntity<String> findAll(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        try {
            beforeFindAll(request, response);
            logSecurityContext();
            Set<E> foundEntities = serviceFindAll();
            Collection<Object> dtos = new HashSet<>();
            for (E e : foundEntities) {
                dtos.add(dtoMapper.mapToDto(e,
                        createDtoClass(coreProperties.controller.endpoints.findAll, Direction.RESPONSE, e)));
            }
            afterFindAll(dtos, foundEntities, request, response);
            String json = jsonMapper.writeValueAsString(dtos);
            return ok(json);
        } catch (BadEntityException e) {
            throw new RuntimeException(e);
        }

    }

    public ResponseEntity<String> find(HttpServletRequest request, HttpServletResponse response) throws IdFetchingException, EntityNotFoundException, BadEntityException, JsonProcessingException {
        ID id = fetchId(request);
        beforeFind(id, request, response);
        logSecurityContext();
        Optional<E> optionalEntity = serviceFind(id);
        VerifyEntity.isPresent(optionalEntity, id, getEntityClass());
        E found = optionalEntity.get();
        Object dto = dtoMapper.mapToDto(
                found,
                createDtoClass(coreProperties.controller.endpoints.find, Direction.RESPONSE, found)
        );
        afterFind(id, dto, optionalEntity, request, response);
        return ok(jsonMapper.writeValueAsString(dto));

    }

    public ResponseEntity<String> create(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IOException {
        String json = readBody(request);
        Class<?> dtoClass = createDtoClass(coreProperties.controller.endpoints.create, Direction.REQUEST, null);
        Object dto = getJsonMapper().readValue(json, dtoClass);
        beforeCreate(dto, request, response);
        validationStrategy.validateDto(dto);
        //i expect that dto has the right dto type -> callers responsibility
        E entity = mapToEntity(dto);
        logSecurityContext();
        E savedEntity = serviceCreate(entity);
        Object resultDto = dtoMapper.mapToDto(savedEntity,
                createDtoClass(coreProperties.controller.endpoints.create, Direction.RESPONSE, savedEntity));
        afterCreate(resultDto, entity, request, response);
        return ok(jsonMapper.writeValueAsString(resultDto));
    }

    public ResponseEntity<String> update(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException, IdFetchingException, JsonPatchException, IOException {
        String patchString = readBody(request);
        log.debug("patchString: " + patchString);
        ID id = fetchId(request);
        //user does also need read permission if he wants to update user, so i can check read permission here instead of using unsecured service
        Optional<E> savedOptional = getService().findById(id);
        VerifyEntity.isPresent(savedOptional, id, getEntityClass());
        E saved = savedOptional.get();
        Class<?> dtoClass = createDtoClass(coreProperties.controller.endpoints.update, Direction.REQUEST, saved);
        beforeUpdate(dtoClass, id, patchString, request, response);

        Object patchDto = dtoMapper.mapToDto(saved, dtoClass);
        patchDto = JsonUtils.applyPatch(patchDto, patchString);
        log.debug("finished patchDto: " + patchDto);
        validationStrategy.validateDto(patchDto);
        E patchEntity = dtoMapper.mapToEntity(patchDto, getEntityClass());
        log.debug("finished patchEntity: " + patchEntity);
        E merged = mergeUpdateStrategy.merge(patchEntity, JpaUtils.detach(saved), dtoClass);
        log.debug("merged Entity as input for service: ");
        logSecurityContext();
        E updated = serviceUpdate(merged, true);
        //no idea why casting is necessary here?
        Class<?> resultDtoClass = createDtoClass(coreProperties.controller.endpoints.update, Direction.RESPONSE, updated);
        Object resultDto = dtoMapper.mapToDto(updated, resultDtoClass);
        afterUpdate(resultDto, updated, request, response);
        return ok(jsonMapper.writeValueAsString(resultDto));
    }

    public ResponseEntity<?> delete(HttpServletRequest request, HttpServletResponse response) throws IdFetchingException, BadEntityException, EntityNotFoundException, ConstraintViolationException {
        ID id = fetchId(request);
        beforeDelete(id, request, response);
        logSecurityContext();
        serviceDelete(id);
        afterDelete(id, request, response);
        return ok();
    }


    //              HELPERS


    public Class<?> createDtoClass(String endpoint, Direction direction, E entity) {
        DtoRequestInfo dtoRequestInfo = createDtoRequestInfo(endpoint, direction, entity);
        return dtoClassLocator.find(dtoRequestInfo);
    }

    protected DtoRequestInfo createDtoRequestInfo(String endpoint, Direction direction, E entity) {
        DtoRequestInfo.Principal principal = currentPrincipal(entity);
        return DtoRequestInfo.builder()
                .authorities(RapidSecurityContext.getRoles())
                .direction(direction)
                .principal(principal)
                .endpoint(endpoint)
                .build();
    }

    protected DtoRequestInfo.Principal currentPrincipal(E entity) {
        DtoRequestInfo.Principal principal = DtoRequestInfo.Principal.ALL;
        if (entity != null) {
            String authenticated = RapidSecurityContext.getName();
            Optional<String> queried = ownerLocator.find(entity);
            if (queried.isPresent() && authenticated != null) {
                principal = queried.get().equals(authenticated)
                        ? DtoRequestInfo.Principal.OWN
                        : DtoRequestInfo.Principal.FOREIGN;
            }
        }
        return principal;
    }

    protected ID fetchId(HttpServletRequest request) throws IdFetchingException {
        ID id = getIdIdFetchingStrategy().fetchId(request);
        getValidationStrategy().validateId(id);
        return id;
    }

    private E mapToEntity(Object dto) throws BadEntityException, EntityNotFoundException {
        return dtoMapper.mapToEntity(dto, entityClass);
    }

    protected String readBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    }

    protected ResponseEntity<String> ok(String jsonDto) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(coreProperties.controller.mediaType))
                .body(jsonDto);
    }

    protected ResponseEntity<?> ok() {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(coreProperties.controller.mediaType))
                .build();
    }

    protected void logSecurityContext() {
        log.debug("SecurityContexts Authentication before service call: " + SecurityContextHolder.getContext().getAuthentication());
    }

    protected void logDtoMappingContext() {
        if (dtoMappingContext != null) {
            log.debug("DtoMappingContext: " + dtoMappingContext.toPrettyString());
        } else {
            log.debug("DtoMappingContext: " + dtoMappingContext);
        }
    }


    //             INIT


    @Autowired
    @SuppressWarnings("unchecked")
    public GenericCrudController() {
        this.entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        preConfigureDtoMappingContextBuilder(dtoMappingContextBuilder);
        this.dtoMappingContext = provideDtoMappingContext(dtoMappingContextBuilder);
        logDtoMappingContext();
        initUrls();
    }

    /**
     * Use one of the {@link com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContextBuilder}s by autowiring them in.
     */
    protected abstract DtoMappingContext provideDtoMappingContext(DTOMappingContextBuilder builder);

    protected void preConfigureDtoMappingContextBuilder(DTOMappingContextBuilder builder) {

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        configureEndpointInfo(endpointInfo);
        try {
            registerEndpoints();
        } catch (NoSuchMethodException e) {
            //should never happen
            throw new RuntimeException(e);
        }

    }

    @Override
    public void afterPropertiesSet() {
        dtoClassLocator.setContext(dtoMappingContext);
        configureDtoClassLocator(dtoClassLocator);
    }

    /**
     * Override this method if you want to register {@link LocalDtoClassLocator}s
     */
    protected void configureDtoClassLocator(DelegatingDtoClassLocator locator) {

    }

    /**
     * Override this method to manage exposure of endpoints
     *
     * @param endpointInfo
     */
    protected void configureEndpointInfo(EndpointInfo endpointInfo) {

    }

    protected String createUrlEntityName(){
        return getEntityClass().getSimpleName().toLowerCase();
    }


    //              URLS


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
    private String entityBaseUrl;
    private String urlEntityName;

    protected void initUrls() {
        this.urlEntityName = createUrlEntityName();
        this.entityBaseUrl = coreProperties.baseUrl + "/" + urlEntityName + "/";
        this.findUrl = entityBaseUrl + coreProperties.controller.endpoints.find;
        this.findAllUrl = entityBaseUrl + coreProperties.controller.endpoints.findAll;
        this.updateUrl = entityBaseUrl + coreProperties.controller.endpoints.update;
        this.deleteUrl = entityBaseUrl + coreProperties.controller.endpoints.delete;
        this.createUrl = entityBaseUrl + coreProperties.controller.endpoints.create;
    }


    //              REGISTER ENDPOINTS


    protected void registerEndpoints() throws NoSuchMethodException {
        if (endpointInfo.isExposeCreate()) {
            registerEndpoint(createCreateRequestMappingInfo(), "create");
        }
        if (endpointInfo.isExposeFind()) {
            registerEndpoint(createFindRequestMappingInfo(), "find");
        }
        if (endpointInfo.isExposeUpdate()) {
            registerEndpoint(createUpdateRequestMappingInfo(), "update");
        }
        if (endpointInfo.isExposeDelete()) {
            registerEndpoint(createDeleteRequestMappingInfo(), "delete");
        }
        if (endpointInfo.isExposeFindAll()) {
            registerEndpoint(createFindAllRequestMappingInfo(), "findAll");
        }
    }

    protected void registerEndpoint(RequestMappingInfo requestMappingInfo, String methodName) throws NoSuchMethodException {
        log.debug("Exposing " + methodName + " Endpoint for " + this.getClass().getSimpleName());
        getEndpointService().addMapping(requestMappingInfo,
                this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class), this);
    }

    protected RequestMappingInfo createFindRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findUrl)
                .methods(RequestMethod.GET)
                .produces(coreProperties.controller.mediaType)
                .build();
    }

    protected RequestMappingInfo createDeleteRequestMappingInfo() {
        return RequestMappingInfo
                .paths(deleteUrl)
                .methods(RequestMethod.DELETE)
                .produces(coreProperties.controller.mediaType)
                .build();
    }

    protected RequestMappingInfo createCreateRequestMappingInfo() {
        return RequestMappingInfo
                .paths(createUrl)
                .methods(RequestMethod.POST)
                .consumes(coreProperties.controller.mediaType)
                .produces(coreProperties.controller.mediaType)
                .build();
    }

    protected RequestMappingInfo createUpdateRequestMappingInfo() {
        return RequestMappingInfo
                .paths(updateUrl)
                .methods(RequestMethod.PUT)
                .consumes(coreProperties.controller.mediaType)
                .produces(coreProperties.controller.mediaType)
                .build();
    }

    protected RequestMappingInfo createFindAllRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findAllUrl)
                .methods(RequestMethod.GET)
                .produces(coreProperties.controller.mediaType)
                .build();
    }


    //              SERVICE CALLBACKS


    protected E serviceUpdate(E update, boolean full) throws BadEntityException, EntityNotFoundException {
        return service.update(update, full);
    }

    protected E serviceCreate(E entity) throws BadEntityException {
        return service.save(entity);
    }

    protected void serviceDelete(ID id) throws BadEntityException, EntityNotFoundException {
        service.deleteById(id);
    }

    protected Set<E> serviceFindAll() {
        return service.findAll();
    }

    protected Optional<E> serviceFind(ID id) throws BadEntityException {
        return service.findById(id);
    }


    //              CONTROLLER CALLBACKS


    public void beforeCreate(Object dto, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void beforeUpdate(Class<?> dtoClass, ID id, String patchString, HttpServletRequest request, HttpServletResponse response) {
    }

    public void beforeDelete(ID id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void beforeFind(ID id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void beforeFindAll(HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterCreate(Object dto, E created, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterUpdate(Object dto, E updated, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterDelete(ID id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterFind(ID id, Object dto, Optional<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterFindAll(Collection<Object> dtos, Set<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }


    //              INJECT DEPENDENCIES


    @Autowired
    @Lazy
    public void injectCrudService(S crudService) {
        this.service = crudService;
    }

    @Autowired
    public void injectMergeUpdateStrategy(MergeUpdateStrategy mergeUpdateStrategy) {
        this.mergeUpdateStrategy = mergeUpdateStrategy;
    }

    @Autowired
    public void injectOwnerLocator(DelegatingOwnerLocator ownerLocator) {
        this.ownerLocator = ownerLocator;
    }

    @Autowired
    public void injectValidationStrategy(ValidationStrategy<ID> validationStrategy) {
        this.validationStrategy = validationStrategy;
    }

    @Autowired
    public void injectDtoClassLocator(DelegatingDtoClassLocator dtoClassLocator) {
        this.dtoClassLocator = dtoClassLocator;
    }

    @Autowired
    public void injectDtoMapper(DelegatingDtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }

    @Autowired
    public void injectEndpointService(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @Autowired
    public void injectEndpointInfo(EndpointInfo endpointInfo) {
        this.endpointInfo = endpointInfo;
    }

    @Autowired
    public void injectDtoMappingContextBuilder(DTOMappingContextBuilder dtoMappingContextBuilder) {
        this.dtoMappingContextBuilder = dtoMappingContextBuilder;
    }

    @Autowired
    public void injectJsonMapper(ObjectMapper mapper) {
        this.jsonMapper = mapper;
    }

    @Autowired
    public void injectIdIdFetchingStrategy(IdFetchingStrategy<ID> idIdFetchingStrategy) {
        this.idIdFetchingStrategy = idIdFetchingStrategy;
    }

    @Autowired
    public void injectCoreProperties(RapidCoreProperties properties) {
        this.coreProperties = properties;
    }
}
