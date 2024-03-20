package com.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.log.LogMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


public abstract class CrudController
        <
                E extends IdAwareEntity<Id>,
                Id extends Serializable,
                CreateDto,
                ResponseDto,
                S extends CrudService<E,Id,CreateDto>
                >
        extends EntityController<E, Id>
{
    private final Log log = LogFactory.getLog(getClass());

    private S service;
    private Class<CreateDto> createDtoClass;
    private Class<ResponseDto> responseDtoClass;
    private IdConverter<Id> idConverter;

    //              CONTROLLER METHODS


    @SuppressWarnings("unchecked")
    public CrudController() {
        super();
        this.createDtoClass = provideCreateDtoClass();
        this.responseDtoClass = provideResponseDtoClass();
    }

    protected Class<CreateDto> provideCreateDtoClass(){
         return (Class<CreateDto>) GenericTypeResolver.resolveTypeArguments(this.getClass(), CrudController.class)[2];
    }

    protected Class<ResponseDto> provideResponseDtoClass(){
        return (Class<ResponseDto>) GenericTypeResolver.resolveTypeArguments(this.getClass(), CrudController.class)[3];
    }

    /**
     * overwrite this method for more complex mapping
     */
    protected ResponseDto mapToResponseDto(E entity){
        return new ModelMapper().map(entity,responseDtoClass);
    }

    public ResponseEntity<ResponseDto> find(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException, JsonProcessingException {
        Id id = fetchId(request);
        log.debug(LogMessage.format("find request received for id %s",id));
        beforeFind(id, request, response);
        Optional<E> optionalEntity = find(id);
        E found = VerifyEntity.isPresent(optionalEntity, id, getEntityClass());
        ResponseDto dto = mapToResponseDto(found);
        afterFind(id, dto, optionalEntity, request, response);
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<Set<ResponseDto>> findSome(HttpServletRequest request, HttpServletResponse response) throws IOException, BadEntityException {
        String json = readBody(request);
        log.debug(LogMessage.format("find some request received for ids '%s'",json));
        CollectionType idSetType = getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, getIdClass());
        Set<Id> ids = getObjectMapper().readValue(json,idSetType);

        beforeFindSome(ids, request, response);
        List<E> foundEntities = findSome(ids);
        Set<ResponseDto> dtos = foundEntities.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toSet());

        afterFindSome(dtos, foundEntities, request, response);
        log.debug("find some request successful");
        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<ResponseDto> create(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IOException {
        log.debug("create request received");
        String json = readBody(request);
        CreateDto dto = getObjectMapper().readValue(json, createDtoClass);
        validateDto(dto);
        beforeCreate(dto, request, response);
        E savedEntity = create(dto);
        ResponseDto responseDto = mapToResponseDto(savedEntity);
        afterCreate(responseDto, savedEntity, request, response);
        log.debug("create request successful");
        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<?> delete(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, ConstraintViolationException {
        Id id = fetchId(request);
        log.debug(LogMessage.format("delete request received for id %s",id));
        beforeDelete(id, request, response);
        delete(id);
        afterDelete(id, request, response);
        log.debug("delete request successful");
        return okNoContent();
    }


    protected Id fetchId(HttpServletRequest request) throws BadEntityException {
        return idConverter.toId(readRequestParam(request,"id"));
    }


    //              URLS


    private String findUrl;
    private String updateUrl;
    private String findAllUrl;
    private String findSomeUrl;
    private String deleteUrl;
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
        if (!getIgnoredEndPoints().contains(getDeleteUrl())) {
            registerEndpoint(createDeleteRequestMappingInfo(), "delete");
        }
        if (!getIgnoredEndPoints().contains(getFindSomeUrl())) {
            registerEndpoint(createFindSomeRequestMappingInfo(), "findSome");
        }
    }

    protected RequestMappingInfo createFindRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    protected RequestMappingInfo createFindSomeRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findSomeUrl)
                .methods(RequestMethod.POST)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


    protected RequestMappingInfo createDeleteRequestMappingInfo() {
        return RequestMappingInfo
                .paths(deleteUrl)
                .methods(RequestMethod.DELETE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    protected RequestMappingInfo createCreateRequestMappingInfo() {
        return RequestMappingInfo
                .paths(createUrl)
                .methods(RequestMethod.POST)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    protected List<String> ignoredEndpoints(){
        return new ArrayList<>();
    }


    //              SERVICE CALLBACKS


    protected E create(CreateDto dto) throws BadEntityException {
        return service.create(dto);
    }

    protected void delete(Id id) throws EntityNotFoundException {
        service.delete(id);
    }

    protected List<E> findSome(Set<Id> ids) {
        return service.findAllById(ids);
    }

    protected Optional<E> find(Id id) {
        return service.findById(id);
    }

    public void setFindUrl(String findUrl) {
        this.findUrl = findUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public void setFindAllUrl(String findAllUrl) {
        this.findAllUrl = findAllUrl;
    }

    public void setFindSomeUrl(String findSomeUrl) {
        this.findSomeUrl = findSomeUrl;
    }

    public void setDeleteUrl(String deleteUrl) {
        this.deleteUrl = deleteUrl;
    }

    public void setCreateUrl(String createUrl) {
        this.createUrl = createUrl;
    }

    //              CONTROLLER CALLBACKS


    public void beforeCreate(Object dto, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }


    public void beforeDelete(Id id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void beforeFind(Id id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void beforeFindSome(Set<Id> ids, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }


    public void afterCreate(Object dto, E created, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterDelete(Id id, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterFind(Id id, Object dto, Optional<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public void afterFindSome(Collection<ResponseDto> dtos, List<E> found, HttpServletRequest httpServletRequest, HttpServletResponse response) {
    }

    public String getCreateUrl() {
        return createUrl;
    }

    public S getService() {
        return service;
    }

    public String getFindUrl() {
        return findUrl;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public String getFindAllUrl() {
        return findAllUrl;
    }

    public String getFindSomeUrl() {
        return findSomeUrl;
    }

    public String getDeleteUrl() {
        return deleteUrl;
    }

    //              INJECT DEPENDENCIES


    public void setIdConverter(IdConverter<Id> idConverter) {
        this.idConverter = idConverter;
    }

    @Autowired
    public void setCrudService(S crudService) {
        this.service = crudService;
    }
}
