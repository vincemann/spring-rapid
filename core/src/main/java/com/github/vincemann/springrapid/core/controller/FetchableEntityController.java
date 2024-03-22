package com.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.log.LogMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;


/**
 * registers endpoints for exposing a specific dto representation of an entity:
 * /api/core/find?id=42
 * /api/core/find-all-by-id (id set in request body in json)
 * /api/core/find-all
 *
 * avoid registering of endpoints by overwriting {@link this#getIgnoredEndPoints()}.
 *
 * @param <E> entity class
 * @param <Id> id type
 * @param <Dto> dto type
 */
public abstract class FetchableEntityController<E,Id extends Serializable,Dto> extends AbstractController implements InitializingBean {


    private IdConverter<Id> idConverter;
    private Class<Id> idClass;
    private Class<?> entityClass;

    private String baseUrl;
    private String findUrl;
    private String findAllByIdUrl;

    private String findAllUrl;

    public FetchableEntityController() {
        this.idClass = provideIdClass();
        this.entityClass = provideEntityClass();
    }

    protected Class<E> provideEntityClass(){
        return  (Class<E>) GenericTypeResolver.resolveTypeArguments(this.getClass(), FetchableEntityController.class)[0];
    }

    protected Class<Id> provideIdClass(){
        return  (Class<Id>) GenericTypeResolver.resolveTypeArguments(this.getClass(), FetchableEntityController.class)[1];
    }

    protected String provideBaseUrl(){
        return "/api/core/"+entityClass.getSimpleName().toLowerCase()+"/";
    }

    public ResponseEntity<List<Dto>> findAll(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, BadEntityException {
        log.debug(LogMessage.format("find all request received"));
        List<Dto> dtos = findAll();
        return ResponseEntity.ok(dtos);
    }

    protected abstract List<Dto> findAll();

    public ResponseEntity<List<Dto>> findAllById(HttpServletRequest request, HttpServletResponse response) throws IOException, BadEntityException {
        String json = readBody(request);
        log.debug(LogMessage.format("find some request received for ids '%s'",json));
        CollectionType idListType = getObjectMapper()
                .getTypeFactory().constructCollectionType(List.class, idClass);
        List<Id> ids = getObjectMapper().readValue(json,idListType);
        log.debug(LogMessage.format("find all by id request received for ids %s",ids));
        List<Dto> dtos = findAllById(ids);
        return ResponseEntity.ok(dtos);
    }

    protected abstract List<Dto> findAllById(List<Id> ids);

    public ResponseEntity<Dto> find(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, JsonProcessingException {
        Id id = idConverter.toId(readRequestParam(request, "id"));
        log.debug(LogMessage.format("find request received for id %s",id));
        Dto dto = find(id);
        return ResponseEntity.ok(dto);
    }

    protected abstract Dto find(Id id);

    @Override
    public void afterPropertiesSet() throws Exception {
        initUrls();
    }

    protected void initUrls(){
        this.baseUrl = provideBaseUrl();
        this.findUrl = baseUrl+"find";
        this.findAllByIdUrl = baseUrl+"find-all-by-id";
        this.findAllUrl = baseUrl+"find-all";
    }

    protected RequestMappingInfo createFindRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    protected RequestMappingInfo createFindAllByIdRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findAllByIdUrl)
                .methods(RequestMethod.POST)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    protected RequestMappingInfo createFindAllRequestMappingInfo() {
        return RequestMappingInfo
                .paths(findAllUrl)
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    protected void registerEndpoints() throws NoSuchMethodException {
        if (!getIgnoredEndPoints().contains(findUrl)) {
            registerEndpoint(createFindRequestMappingInfo(), "find");
        }
        if (!getIgnoredEndPoints().contains(findAllByIdUrl)) {
            registerEndpoint(createFindAllByIdRequestMappingInfo(), "findAllById");
        }
        if (!getIgnoredEndPoints().contains(findAllUrl)) {
            registerEndpoint(createFindAllRequestMappingInfo(), "findAll");
        }
    }
}
