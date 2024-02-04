package com.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.vincemann.springrapid.core.controller.dto.DtoClassLocator;
import com.github.vincemann.springrapid.core.controller.dto.DtoValidationStrategy;
import com.github.vincemann.springrapid.core.controller.dto.MergeUpdateStrategy;
import com.github.vincemann.springrapid.core.controller.dto.map.*;
import com.github.vincemann.springrapid.core.controller.id.IdFetchingException;
import com.github.vincemann.springrapid.core.controller.id.IdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.json.JsonDtoPropertyValidator;
import com.github.vincemann.springrapid.core.controller.json.patch.JsonPatchStrategy;
import com.github.vincemann.springrapid.core.controller.json.patch.PatchInfo;
import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
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

import static com.github.vincemann.springrapid.core.controller.WebExtensionType.*;


@Slf4j
@Getter
public abstract class CrudController<E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends AbstractEntityController<E, Id>
{


    private IdFetchingStrategy<Id> idFetchingStrategy;
    private CrudService<E, Id> service;
    private DelegatingDtoMapper dtoMapper;
    private DelegatingOwnerLocator ownerLocator;
    private DtoClassLocator dtoClassLocator;
    private DtoMappings dtoMappings;
    private DtoValidationStrategy dtoValidationStrategy;
    private MergeUpdateStrategy mergeUpdateStrategy;
    private JsonPatchStrategy jsonPatchStrategy;
    private JsonDtoPropertyValidator jsonDtoPropertyValidator;
    private PrincipalFactory principalFactory;


    //              CONTROLLER METHODS

    public ResponseEntity<String> findAll(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, BadEntityException {


        List<QueryFilter<? super E>> queryFilters = extractExtensions(request,QUERY_FILTER);
        List<EntityFilter<? super E>> entityFilters = extractExtensions(request,ENTITY_FILTER);
        List<SortingExtension> sorting = extractExtensions(request,SORTING);

        beforeFindAll(request, response,entityFilters,queryFilters,sorting);
        logSecurityContext();
        Set<E> foundEntities = findAll(queryFilters, entityFilters,sorting);
        List<Object> dtos = new ArrayList<>();
        for (E e : foundEntities) {
            dtos.add(dtoMapper.mapToDto(e,
                    createDtoClass(getFindAllUrl(), Direction.RESPONSE,request, e)));
        }
        afterFindAll(dtos, foundEntities, request, response,entityFilters, queryFilters, sorting);
        String json = jsonMapper.writeDto(dtos);
        return ok(json);

    }

    public ResponseEntity<String> findSome(HttpServletRequest request, HttpServletResponse response) throws IOException, BadEntityException {
        logSecurityContext();

        String json = readBody(request);
        CollectionType idSetType = getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, getIdClass());
        Set<Id> ids = getJsonMapper().readDto(json,idSetType);



        beforeFindSome(ids, request, response);

        Set<E> foundEntities = findSome(ids);
        List<Object> dtos = new ArrayList<>();
        for (E e : foundEntities) {
            dtos.add(dtoMapper.mapToDto(e,
                    createDtoClass(getFindSomeUrl(), Direction.RESPONSE,request, e)));
        }
        afterFindSome(dtos, foundEntities, request, response);
        String responseJson = jsonMapper.writeDto(dtos);
        return ok(responseJson);
    }


    public ResponseEntity<String> find(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException, JsonProcessingException {

        Id id = fetchId(request);
        beforeFind(id, request, response);
        logSecurityContext();
        Optional<E> optionalEntity = find(id);
        E found = VerifyEntity.isPresent(optionalEntity, id, getEntityClass());
        Object dto = dtoMapper.mapToDto(
                found,
                createDtoClass(getFindUrl(), Direction.RESPONSE,request, found)
        );
        afterFind(id, dto, optionalEntity, request, response);
        return ok(jsonMapper.writeDto(dto));

    }

    public ResponseEntity<String> create(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IOException {

        String json = readBody(request);
        Class<?> dtoClass = createDtoClass(getCreateUrl(), Direction.REQUEST, request,null);
        jsonDtoPropertyValidator.validateDto(json, dtoClass);
        Object dto = getJsonMapper().readDto(json, dtoClass);
        beforeCreate(dto, request, response);
        dtoValidationStrategy.validate(dto);
        //i expect that dto has the right dto type -> callers responsibility
        E entity = mapToEntity(dto);
        logSecurityContext();
        E savedEntity = create(entity);
        Object resultDto = dtoMapper.mapToDto(savedEntity,
                createDtoClass(getCreateUrl(), Direction.RESPONSE,request, savedEntity));
        afterCreate(resultDto, entity, request, response);
        return ok(jsonMapper.writeDto(resultDto));
    }

    public ResponseEntity<String> update(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException, JsonPatchException, IOException {
        

        String patchString = readBody(request);
        log.debug("patchString: " + patchString);
        Id id = fetchId(request);
        //user does also need read permission if he wants to update user, so I can check read permission here instead of using unsecured service
        // i indirectly check if by using secured service.findById
        Optional<E> savedOptional = getService().findById(id);
        E saved = VerifyEntity.isPresent(savedOptional, id, getEntityClass());
        Class<?> dtoClass = createDtoClass(getUpdateUrl(), Direction.REQUEST,request, saved);
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
        Class<?> resultDtoClass = createDtoClass(getUpdateUrl(), Direction.RESPONSE,request, updated);
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

    public ResponseEntity<?> delete(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, ConstraintViolationException {
        Id id = fetchId(request);
        beforeDelete(id, request, response);
        logSecurityContext();
        delete(id);
        afterDelete(id, request, response);
        return okNoContent();
    }

    //              HELPERS



    protected Class<?> createDtoClass(String endpoint, Direction direction, HttpServletRequest request, E entity) throws BadEntityException {
        DtoRequestInfo dtoRequestInfo = createDtoRequestInfo(endpoint, direction,request, entity);
        return dtoClassLocator.find(dtoRequestInfo,dtoMappings);
    }

    protected DtoRequestInfo createDtoRequestInfo(String endpoint, Direction direction,HttpServletRequest request, E entity) {
        Principal principal = principalFactory.create(entity);
        return DtoRequestInfo.builder()
                .authorities(RapidSecurityContext.getRoles())
                .direction(direction)
                .request(request)
                .principal(principal)
                .endpoint(endpoint)
                .build();
    }


    protected Id fetchId(HttpServletRequest request) throws IdFetchingException {
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
        log.debug("security context principal before service call: " + SecurityContextHolder.getContext().getAuthentication());
    }


    //             INIT


    @SuppressWarnings("unchecked")
    public CrudController() {
        super();
    }


    /**
     * implement this method in order to set request -> dto-class mappings.
     *
     * example:
     *  @Override
     *     protected void configureDtoMappings(DtoMappingsBuilder builder) {
     *
     *         builder.when(endpoint(getCreateUrl()).and(direction(Direction.REQUEST)))
     *                 .thenReturn(CreateOwnerDto.class);
     *
     *         builder.when(endpoint(getUpdateUrl()).and(direction(Direction.REQUEST)))
     *                 .thenReturn(UpdateOwnerDto.class);
     *
     *         builder.when(direction(Direction.RESPONSE).and(principal(Principal.OWN)))
     *                 .thenReturn(ReadOwnOwnerDto.class);
     *     }
     *
     * you can combine {@link DtoMappingConditions} with your own predicates.
     * Note:
     * order is important, the first match counts, make sure to put fallback mappings at the end.
     *
     */
    protected abstract void configureDtoMappings(DtoMappingsBuilder builder);

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
        if (!getIgnoredEndPoints().contains(getCreateUrl())) {
            registerEndpoint(createCreateRequestMappingInfo(), "create");
        }
        if (!getIgnoredEndPoints().contains(getFindUrl())) {
            registerEndpoint(createFindRequestMappingInfo(), "find");
        }
        if (!getIgnoredEndPoints().contains(getUpdateUrl())) {
            registerEndpoint(createUpdateRequestMappingInfo(), "update");
        }
        if (!getIgnoredEndPoints().contains(getDeleteUrl())) {
            registerEndpoint(createDeleteRequestMappingInfo(), "delete");
        }
        if (!getIgnoredEndPoints().contains(getFindAllUrl())) {
            registerEndpoint(createFindAllRequestMappingInfo(), "findAll");
        }
        if (!getIgnoredEndPoints().contains(getFindSomeUrl())) {
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

    protected List<String> ignoredEndpoints(){
        return new ArrayList<>();
    }


    //              SERVICE CALLBACKS


    protected E servicePartialUpdate(E update, String... propertiesToDelete) throws BadEntityException, EntityNotFoundException {
        return service.partialUpdate(update, propertiesToDelete);
    }

    protected E create(E entity) throws BadEntityException {
        return service.save(entity);
    }

    protected void delete(Id id) throws EntityNotFoundException {
        service.deleteById(id);
    }

    protected Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> filters, List<SortingExtension> sortingStrategies) {
        if (filters.isEmpty() && jpqlFilters.isEmpty() && sortingStrategies.isEmpty())
            return service.findAll();
        else
            return service.findAll(jpqlFilters,filters,sortingStrategies);
    }

    protected Set<E> findSome(Set<Id> ids) {
        return service.findSome(ids);
    }

    protected Optional<E> find(Id id) {
        return service.findById(id);
    }


    //              CONTROLLER CALLBACKS


    public void beforeCreate(Object dto, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void beforeUpdate(Class<?> dtoClass, Id id, String patchString, HttpServletRequest request, HttpServletResponse response) {
    }

    public void beforeDelete(Id id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void beforeFind(Id id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void beforeFindAll(HttpServletRequest httpServletRequest, HttpServletResponse response, List<EntityFilter<? super E>> filters, List<QueryFilter<? super E>> jpqlFilters, List<SortingExtension> sortingStrategies) {
    }

    public void beforeFindSome(Set<Id> ids, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }


    public void afterCreate(Object dto, E created, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterUpdate(Object dto, E updated, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterDelete(Id id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterFind(Id id, Object dto, Optional<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterFindAll(Collection<Object> dtos, Set<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response, List<EntityFilter<? super E>> filters, List<QueryFilter<? super E>> jpqlFilters, List<SortingExtension> sortingStrategies) {
    }

    public void afterFindSome(Collection<Object> dtos, Set<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }


    //              INJECT DEPENDENCIES


    @Autowired
    @Lazy
    public void setCrudService(CrudService<E, Id> crudService) {
        this.service = crudService;
    }

    @Autowired
    public void setMergeUpdateStrategy(MergeUpdateStrategy mergeUpdateStrategy) {
        this.mergeUpdateStrategy = mergeUpdateStrategy;
    }

    @Autowired
    public void setJsonPatchStrategy(JsonPatchStrategy jsonPatchStrategy) {
        this.jsonPatchStrategy = jsonPatchStrategy;
    }

    @Autowired
    public void setOwnerLocator(DelegatingOwnerLocator ownerLocator) {
        this.ownerLocator = ownerLocator;
    }

    @Autowired
    public void setValidationStrategy(DtoValidationStrategy dtoValidationStrategy) {
        this.dtoValidationStrategy = dtoValidationStrategy;
    }

    @Autowired
    public void setDtoClassLocator(DtoClassLocator dtoClassLocator) {
        this.dtoClassLocator = dtoClassLocator;
    }

    @Autowired
    public void setDtoMapper(DelegatingDtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }

    @Autowired
    public void setIdIdFetchingStrategy(IdFetchingStrategy<Id> idFetchingStrategy) {
        this.idFetchingStrategy = idFetchingStrategy;
    }

    @Autowired
    public void setJsonDtoPropertyValidator(JsonDtoPropertyValidator jsonDtoPropertyValidator) {
        this.jsonDtoPropertyValidator = jsonDtoPropertyValidator;
    }

    @Autowired
    public void setPrincipalFactory(PrincipalFactory principalFactory) {
        this.principalFactory = principalFactory;
    }
}
