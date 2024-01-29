package com.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.vincemann.springrapid.core.controller.dto.DtoClassLocator;
import com.github.vincemann.springrapid.core.controller.dto.DtoValidationStrategy;
import com.github.vincemann.springrapid.core.controller.dto.mapper.*;
import com.github.vincemann.springrapid.core.controller.fetchid.IdFetchingException;
import com.github.vincemann.springrapid.core.controller.fetchid.IdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.json.JsonDtoPropertyValidator;
import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.EntityReflectionUtils;
import com.github.vincemann.springrapid.core.util.IdPropertyNameUtils;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
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
import java.util.*;

import static com.github.vincemann.springrapid.core.util.HttpServletRequestUtils.getRequestParameterKeysWithoutValue;


@Slf4j
@Getter
public abstract class CrudEntityController<E extends IdentifiableEntity<ID>, ID extends Serializable>
        extends AbstractEntityController<E,ID>
{


    //              DEPENDENCIES


    private IdFetchingStrategy<ID> idFetchingStrategy;
    private CrudService<E, ID> service;
    private DelegatingDtoMapper dtoMapper;
    private DelegatingOwnerLocator ownerLocator;
    private DtoClassLocator dtoClassLocator;
    private DtoMappings dtoMappings;
    private DtoValidationStrategy dtoValidationStrategy;
    private MergeUpdateStrategy mergeUpdateStrategy;
    private JsonPatchStrategy jsonPatchStrategy;
    private JsonDtoPropertyValidator jsonDtoPropertyValidator;
    private PrincipalFactory principalFactory;

    private List<String> ignoredEndPoints = new ArrayList<>();



    //              CONTROLLER METHODS

    public ResponseEntity<String> findAll(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, BadEntityException {

        List<String> noValParams = getRequestParameterKeysWithoutValue(request);

        List<QueryFilter<? super E>> queryFilters = extractExtensions(request,QUERY_FILTER_URL_KEY);
        List<EntityFilter<? super E>> entityFilters = extractExtensions(request,ENTITY_FILTER_URL_KEY);
        List<SortingExtension> sortingStrategies = extractExtensions(request,ENTITY_SORTING_STRATEGY_URL_KEY);

        beforeFindAll(request, response,entityFilters,queryFilters,sortingStrategies);
        logSecurityContext();
        Set<E> foundEntities = serviceFindAll(queryFilters, entityFilters,sortingStrategies);
        List<Object> dtos = new ArrayList<>();
        for (E e : foundEntities) {
            dtos.add(dtoMapper.mapToDto(e,
                    createDtoClass(getFindAllUrl(), Direction.RESPONSE,noValParams, e)));
        }
        afterFindAll(dtos, foundEntities, request, response,entityFilters, queryFilters, sortingStrategies);
        String json = jsonMapper.writeDto(dtos);
        return ok(json);

    }

    public ResponseEntity<String> findSome(HttpServletRequest request, HttpServletResponse response) throws IOException, BadEntityException {
        logSecurityContext();

        List<String> noValParams = getRequestParameterKeysWithoutValue(request);

        String json = readBody(request);
        CollectionType idSetType = getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, getIdClass());
        Set<ID> ids = getJsonMapper().readDto(json,idSetType);



        beforeFindSome(ids, request, response);

        Set<E> foundEntities = serviceFindSome(ids);
        List<Object> dtos = new ArrayList<>();
        for (E e : foundEntities) {
            dtos.add(dtoMapper.mapToDto(e,
                    createDtoClass(getFindSomeUrl(), Direction.RESPONSE,noValParams, e)));
        }
        afterFindSome(dtos, foundEntities, request, response);
        String responseJson = jsonMapper.writeDto(dtos);
        return ok(responseJson);
    }


    public ResponseEntity<String> find(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException, JsonProcessingException {

        List<String> noValParams = getRequestParameterKeysWithoutValue(request);

        ID id = fetchId(request);
        beforeFind(id, request, response);
        logSecurityContext();
        Optional<E> optionalEntity = serviceFind(id);
        E found = VerifyEntity.isPresent(optionalEntity, id, getEntityClass());
        Object dto = dtoMapper.mapToDto(
                found,
                createDtoClass(getFindUrl(), Direction.RESPONSE,noValParams, found)
        );
        afterFind(id, dto, optionalEntity, request, response);
        return ok(jsonMapper.writeDto(dto));

    }

    public ResponseEntity<String> create(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IOException {

        List<String> noValParams = getRequestParameterKeysWithoutValue(request);

        String json = readBody(request);
        Class<?> dtoClass = createDtoClass(getCreateUrl(), Direction.REQUEST, noValParams,null);
        jsonDtoPropertyValidator.validateDto(json, dtoClass);
        Object dto = getJsonMapper().readDto(json, dtoClass);
        beforeCreate(dto, request, response);
        dtoValidationStrategy.validate(dto);
        //i expect that dto has the right dto type -> callers responsibility
        E entity = mapToEntity(dto);
        logSecurityContext();
        E savedEntity = serviceCreate(entity);
        Object resultDto = dtoMapper.mapToDto(savedEntity,
                createDtoClass(getCreateUrl(), Direction.RESPONSE,noValParams, savedEntity));
        afterCreate(resultDto, entity, request, response);
        return ok(jsonMapper.writeDto(resultDto));
    }

    public ResponseEntity<String> update(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException, JsonPatchException, IOException {

        List<String> noValParams = getRequestParameterKeysWithoutValue(request);

        String patchString = readBody(request);
        log.debug("patchString: " + patchString);
        ID id = fetchId(request);
        //user does also need read permission if he wants to update user, so I can check read permission here instead of using unsecured service
        // i indirectly check if by using secured service.findById
        Optional<E> savedOptional = getService().findById(id);
        E saved = VerifyEntity.isPresent(savedOptional, id, getEntityClass());
        Class<?> dtoClass = createDtoClass(getUpdateUrl(), Direction.REQUEST,noValParams, saved);
        beforeUpdate(dtoClass, id, patchString, request, response);
        jsonDtoPropertyValidator.validatePatch(patchString, dtoClass);


        PatchInfo patchInfo = jsonPatchStrategy.createPatchInfo(patchString);
        Object patchDto = dtoMapper.mapToDto(saved, dtoClass, patchInfo.getUpdatedFields().toArray(new String[0]));
        patchDto = jsonPatchStrategy.applyPatch(patchDto, patchString);
        // map to dto mapped schon nur die updated properties, also muss es bei mapToEntity nicht erneut limited werden auf mapped properties
        E patchEntity = dtoMapper.mapToEntity(patchDto, getEntityClass());
        // some dtos might not have id set, so we add it here
        patchEntity.setId(id);
        // set all non updated fields to null, to avoid i.E. roles = new HashSet() in class initializer beeing interpreted as an update to remove all roles!
        Set<String> allUpdatedFields = IdPropertyNameUtils.transformIdFieldNamesToSet(patchInfo.getAllUpdatedFields());
        // id field can be any name!
//        allUpdatedFields.add("id");
        allUpdatedFields.add(IdPropertyNameUtils.findIdFieldName(patchEntity.getClass()));
        EntityReflectionUtils.setNonMatchingFieldsNull(patchEntity, allUpdatedFields);
        Set<String> relevantFields = new HashSet<>(allUpdatedFields);
        relevantFields.addAll(IdPropertyNameUtils.transformIdFieldNames(patchInfo.getRemoveSingleMembersFields()));

//        Set<String> updatedCollectionFields = EntityReflectionUtils.findCollectionFields(allUpdatedFields, getEntityClass());


        logSecurityContext();
        E updated = servicePartialUpdate(patchEntity, relevantFields.toArray(new String[0]));
        Class<?> resultDtoClass = createDtoClass(getUpdateUrl(), Direction.RESPONSE,noValParams, updated);
        // no third arg, bc mapping all possible fields
        Object resultDto = dtoMapper.mapToDto(updated, resultDtoClass);
        afterUpdate(resultDto, updated, request, response);
        return ok(jsonMapper.writeDto(resultDto));


        //        E merged = mergeUpdateStrategy.merge(patchEntity, JpaUtils.detach(saved), dtoClass);
//        patchDto = jsonPatchStrategy.applyPatch(patchDto, patchString);
//        log.debug("finished patchDto: " + patchDto);
//        dtoValidationStrategy.validate(patchDto);
//        E patchEntity = dtoMapper.mapToEntity(patchDto, getEntityClass());
//        log.debug("finished patchEntity: " + patchEntity);
//        // merge dto fields. patch merged with saved Entity.
//        E merged = mergeUpdateStrategy.merge(patchEntity, JpaUtils.detach(saved), dtoClass);
    }


    protected abstract void configureDtoMappings(DtoMappingsBuilder builder);

    public ResponseEntity<?> delete(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, ConstraintViolationException {
        ID id = fetchId(request);
        beforeDelete(id, request, response);
        logSecurityContext();
        serviceDelete(id);
        afterDelete(id, request, response);
        return okNoContent();
    }

    //              HELPERS


    protected Class<?> createDtoClass(String endpoint, Direction direction, List<String> urlParams, E entity) throws BadEntityException {
        DtoRequestInfo dtoRequestInfo = createDtoRequestInfo(endpoint, direction,urlParams, entity);
        return dtoClassLocator.find(dtoRequestInfo,dtoMappings);
    }

    protected DtoRequestInfo createDtoRequestInfo(String endpoint, Direction direction,List<String> urlParams, E entity) {
        Principal principal = principalFactory.create(entity);
        return DtoRequestInfo.builder()
                .authorities(RapidSecurityContext.getRoles())
                .direction(direction)
                .urlParams(urlParams)
                .principal(principal)
                .endpoint(endpoint)
                .build();
    }


    protected ID fetchId(HttpServletRequest request) throws IdFetchingException {
        return this.getIdFetchingStrategy().fetchId(request);
    }

    private E mapToEntity(Object dto) throws BadEntityException, EntityNotFoundException {
        return dtoMapper.mapToEntity(dto, getEntityClass());
    }


    protected String readRequestParam(HttpServletRequest request, String key) throws BadEntityException {
        String param = request.getParameter(key);
        if (param == null) {
            throw new BadEntityException("RequestParam with key: " + key + " not found");
        } else {
            return param;
        }
    }

    protected Optional<String> readOptionalRequestParam(HttpServletRequest request, String key) {
        String param = request.getParameter(key);
        if (param != null) {
            return Optional.of(param);
        } else {
            return Optional.empty();
        }
    }


    protected ResponseEntity<String> ok(String jsonDto) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(coreProperties.getController().getMediaType()))
                .body(jsonDto);
    }

    protected List<String> ignoredEndpoints(){
        return new ArrayList<>();
    }

    protected ResponseEntity<?> okNoContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

//    protected ResponseEntity<String> okCreated(String jsonDto) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .contentType(MediaType.valueOf(coreProperties.getController().getMediaType()))
//                .body(jsonDto);
//    }

    protected ResponseEntity<?> ok() {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(coreProperties.getController().getMediaType()))
                .build();
    }

    protected void logSecurityContext() {
        log.debug("SecurityContexts Authentication before service call: " + SecurityContextHolder.getContext().getAuthentication());
    }


    //             INIT


    @SuppressWarnings("unchecked")
    public CrudEntityController() {
        super();
    }


    protected String getMediaType() {
        return coreProperties.getController().getMediaType();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        DtoMappingsBuilder builder = new DtoMappingsBuilder();
        configureDtoMappings(builder);
        this.dtoMappings = builder.build();
        // todo implement to string
        log.debug("dto mappings: " + dtoMappings);
    }
    

    //              URLS

    @Setter
    private String findUrl;
    @Setter
    private String updateUrl;
    @Setter
    private String findAllUrl;
    @Setter
    private String findSomeUrl;
    @Setter
    private String deleteUrl;
    @Setter
    private String createUrl;

    protected void initUrls() {
        super.initUrls();
        this.findUrl = entityBaseUrl + coreProperties.controller.endpoints.find;
        this.findAllUrl = entityBaseUrl + coreProperties.controller.endpoints.findAll;
        this.findSomeUrl = entityBaseUrl + coreProperties.controller.endpoints.findSome;
        this.updateUrl = entityBaseUrl + coreProperties.controller.endpoints.update;
        this.deleteUrl = entityBaseUrl + coreProperties.controller.endpoints.delete;
        this.createUrl = entityBaseUrl + coreProperties.controller.endpoints.create;
    }


    //              REGISTER ENDPOINTS


    @Override
    protected void registerEndpoints() throws NoSuchMethodException {
        if (ignoredEndPoints.contains(getCreateUrl())) {
            registerEndpoint(createCreateRequestMappingInfo(), "create");
        }
        if (ignoredEndPoints.contains(getFindUrl())) {
            registerEndpoint(createFindRequestMappingInfo(), "find");
        }
        if (ignoredEndPoints.contains(getUpdateUrl())) {
            registerEndpoint(createUpdateRequestMappingInfo(), "update");
        }
        if (ignoredEndPoints.contains(getDeleteUrl())) {
            registerEndpoint(createDeleteRequestMappingInfo(), "delete");
        }
        if (ignoredEndPoints.contains(getFindAllUrl())) {
            registerEndpoint(createFindAllRequestMappingInfo(), "findAll");
        }
        if (ignoredEndPoints.contains(getFindSomeUrl())) {
            registerEndpoint(createFindSomeRequestMappingInfo(), "findSome");
        }
    }

    protected RequestMappingInfo createFindRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findUrl)
                .methods(RequestMethod.GET)
                .produces(getMediaType())
                .build();
    }

    protected RequestMappingInfo createFindSomeRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findSomeUrl)
                .methods(RequestMethod.POST)
                .produces(getMediaType())
                .build();
    }


    protected RequestMappingInfo createDeleteRequestMappingInfo() {
        return RequestMappingInfo
                .paths(deleteUrl)
                .methods(RequestMethod.DELETE)
                .produces(getMediaType())
                .build();
    }

    protected RequestMappingInfo createCreateRequestMappingInfo() {
        return RequestMappingInfo
                .paths(createUrl)
                .methods(RequestMethod.POST)
                .consumes(getMediaType())
                .produces(getMediaType())
                .build();
    }

    protected RequestMappingInfo createUpdateRequestMappingInfo() {
        return RequestMappingInfo
                .paths(updateUrl)
                .methods(RequestMethod.PUT)
                .consumes(getMediaType())
                .produces(getMediaType())
                .build();
    }

    protected RequestMappingInfo createFindAllRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findAllUrl)
                .methods(RequestMethod.GET)
                .produces(getMediaType())
                .build();
    }


    //              SERVICE CALLBACKS


    protected E servicePartialUpdate(E update, String... propertiesToDelete) throws BadEntityException, EntityNotFoundException {
        return service.partialUpdate(update, propertiesToDelete);
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

    protected Set<E> serviceFindAll(List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> filters, List<SortingExtension> sortingStrategies) {
        if (filters.isEmpty() && jpqlFilters.isEmpty() && sortingStrategies.isEmpty())
            return service.findAll();
        else
            return service.findAll(jpqlFilters,filters,sortingStrategies);
    }

    protected Set<E> serviceFindSome(Set<ID> ids) {
        return service.findSome(ids);
    }

    protected Optional<E> serviceFind(ID id) {
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

    public void beforeFindAll(HttpServletRequest httpServletRequest, HttpServletResponse response, List<EntityFilter<? super E>> filters, List<QueryFilter<? super E>> jpqlFilters, List<SortingExtension> sortingStrategies) {
    }

    public void beforeFindSome(Set<ID> ids, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }


    public void afterCreate(Object dto, E created, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterUpdate(Object dto, E updated, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterDelete(ID id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterFind(ID id, Object dto, Optional<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterFindAll(Collection<Object> dtos, Set<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response, List<EntityFilter<? super E>> filters, List<QueryFilter<? super E>> jpqlFilters, List<SortingExtension> sortingStrategies) {
    }

    public void afterFindSome(Collection<Object> dtos, Set<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }


    //              INJECT DEPENDENCIES


    @Autowired
    @Lazy
    public void injectCrudService(CrudService<E,ID> crudService) {
        this.service = crudService;
    }

    @Autowired
    public void injectMergeUpdateStrategy(MergeUpdateStrategy mergeUpdateStrategy) {
        this.mergeUpdateStrategy = mergeUpdateStrategy;
    }

    @Autowired
    public void injectJsonPatchStrategy(JsonPatchStrategy jsonPatchStrategy) {
        this.jsonPatchStrategy = jsonPatchStrategy;
    }

    @Autowired
    public void injectOwnerLocator(DelegatingOwnerLocator ownerLocator) {
        this.ownerLocator = ownerLocator;
    }

    @Autowired
    public void injectValidationStrategy(DtoValidationStrategy dtoValidationStrategy) {
        this.dtoValidationStrategy = dtoValidationStrategy;
    }

    @Autowired
    public void injectDtoClassLocator(DtoClassLocator dtoClassLocator) {
        this.dtoClassLocator = dtoClassLocator;
    }

    @Autowired
    public void injectDtoMapper(DelegatingDtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }

    @Autowired
    public void injectIdIdFetchingStrategy(IdFetchingStrategy<ID> idFetchingStrategy) {
        this.idFetchingStrategy = idFetchingStrategy;
    }

    @Autowired
    public void injectJsonDtoPropertyValidator(JsonDtoPropertyValidator jsonDtoPropertyValidator) {
        this.jsonDtoPropertyValidator = jsonDtoPropertyValidator;
    }

    @Autowired
    public void injectPrincipalFactory(PrincipalFactory principalFactory) {
        this.principalFactory = principalFactory;
    }
}
