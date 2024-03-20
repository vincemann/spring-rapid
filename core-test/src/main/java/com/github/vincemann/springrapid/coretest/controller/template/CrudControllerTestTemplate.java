package com.github.vincemann.springrapid.coretest.controller.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class CrudControllerTestTemplate<C>
        extends MvcControllerTestTemplate<C>
{

    protected Class<?> entityClass;
    private String urlStart;


    public CrudControllerTestTemplate(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.urlStart = provideUrlStart();
    }

    protected String provideUrlStart(){
        return "/api/core/"+entityClass.getSimpleName().toLowerCase()+"/";
    }

    protected String getDeleteUrl(){
        return urlStart + "delete";
    }

    protected String getCreateUrl(){
        return urlStart + "create";
    }

    protected String getFindUrl(){
        return urlStart + "find";
    }

    protected String getFindSomeUrl(){
        return urlStart + "find-some";
    }


    // CRUD CONTROLLER METHODS

    public MockHttpServletRequestBuilder delete(Serializable id) throws Exception {
        return MockMvcRequestBuilders.delete(getDeleteUrl())
                .param("id",id.toString());
    }

    public MockHttpServletRequestBuilder find(Serializable id) throws Exception {
        return get(getFindUrl())
                .param("id",id.toString());
    }

    public <D> D find2xx(Serializable id, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(find(id),dtoClass);
    }

    public <D> D find2xx(Serializable id,String token, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(withToken(find(id),token),dtoClass);
    }


    public  MockHttpServletRequestBuilder create(Object dto) throws Exception {
        return post(getCreateUrl())
                .content(serialize(dto))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    public <D> D create2xx(Object dto, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(create(dto),dtoClass);
    }

    public <D> D create2xx(Object dto,String token, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(withToken(create(dto),token),dtoClass);
    }

    public  MockHttpServletRequestBuilder findSome(Set<String> ids) throws Exception {
        return post(getFindSomeUrl())
                .content(serialize(ids))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    public <D> Set<D> findSome2xx(Set<String> ids, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserializeToSet(findSome(ids),dtoClass);
    }
}
