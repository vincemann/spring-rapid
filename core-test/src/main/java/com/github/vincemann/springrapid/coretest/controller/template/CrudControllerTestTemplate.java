package com.github.vincemann.springrapid.coretest.controller.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.coretest.controller.UrlWebExtension;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Getter
public abstract class CrudControllerTestTemplate<C extends CrudController>
        extends MvcControllerTestTemplate<C>
{

    @Autowired
    private ApplicationContext applicationContext;


    // CRUD CONTROLLER METHODS

    public MockHttpServletRequestBuilder delete(Serializable id) throws Exception {
        return MockMvcRequestBuilders.delete(controller.getDeleteUrl())
                .param("id",id.toString());
    }

    public MockHttpServletRequestBuilder find(Serializable id) throws Exception {
        return get(controller.getFindUrl())
                .param("id",id.toString());
    }

    public <D> D find2xx(Serializable id, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(find(id),dtoClass);
    }

    public <D> D find2xx(Serializable id,String token, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(withToken(find(id),token),dtoClass);
    }

    public MockHttpServletRequestBuilder update(String patchString,Serializable id) throws Exception {
        return put(controller.getUpdateUrl())
                .param("id",id.toString())
                .content(patchString)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    public <D> D update2xx(String patchString,Serializable id, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(update(patchString,id),dtoClass);
    }

    public <D> D update2xx(String patchString,Serializable id,String token, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(withToken(update(patchString,id),token),dtoClass);
    }


    public  MockHttpServletRequestBuilder create(Object dto) throws Exception {
        return post(controller.getCreateUrl())
                .content(serialize(dto))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    public <D> D create2xx(Object dto, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(create(dto),dtoClass);
    }

    public <D> D create2xx(Object dto,String token, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserialize(withToken(create(dto),token),dtoClass);
    }


    public  MockHttpServletRequestBuilder findAll() throws Exception {
        return get(getController().getFindAllUrl());
    }

    public <D> Set<D> findAll2xx(Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserializeToSet(findAll(),dtoClass);
    }

    public  MockHttpServletRequestBuilder findSome(Set<String> ids) throws Exception {
        return post(controller.getFindSomeUrl())
                .content(getController().getObjectMapper().writeDto(ids))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    public <D> Set<D> findSome2xx(Set<String> ids, Class<D> dtoClass) throws Exception {
        return perform2xxAndDeserializeToSet(findSome(ids),dtoClass);
    }


    public MockHttpServletRequestBuilder findAll(UrlWebExtension... extensions) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(controller.getFindAllUrl());
        RapidTestUtil.addUrlExtensionsToRequest(applicationContext,requestBuilder,extensions);
        return requestBuilder;
    }

    public <D> List<D> findAll2xx(Class<D> dtoClass, UrlWebExtension... extensions) throws Exception {
        return perform2xxAndDeserializeToList(findAll(extensions),dtoClass);
    }



    // MVC PERFORM


    /**
     * perform, expect 2xx and deserialize result to dtoClass
     */
    public <Dto> Dto perform2xxAndDeserialize(MockHttpServletRequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
        return performAndDeserialize(requestBuilder,status().is2xxSuccessful(),dtoClass);
    }


    public <Dto> Dto performAndDeserialize(MockHttpServletRequestBuilder requestBuilder, ResultMatcher status, Class<Dto> dtoClass) throws Exception {
        return deserialize(mvc.perform(requestBuilder)
                .andExpect(status)
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }

    public <Dto> Set<Dto> perform2xxAndDeserializeToSet(MockHttpServletRequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
        return deserializeToSet(mvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }
    public <Dto> List<Dto> perform2xxAndDeserializeToList(MockHttpServletRequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
        return deserializeToList(mvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }

    public static MockHttpServletRequestBuilder withToken(MockHttpServletRequestBuilder builder, String token){
        return builder.header(HttpHeaders.AUTHORIZATION,token);
    }


    // SERIALIZATION


    public  <Dto> Dto deserialize(String s, Class<Dto> dtoClass) throws IOException {
        return controller.getObjectMapper().readDto(s,dtoClass);
    }

    public  <Dto> Set<Dto> deserializeToSet(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = controller.getObjectMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, dtoClass);
        return deserialize(s, setType);
    }

    public  <Dto> List<Dto> deserializeToList(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = controller.getObjectMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(List.class, dtoClass);
        return deserialize(s, setType);
    }

    public  <Dto> Dto deserialize(String s, TypeReference<?> dtoClass) throws IOException {
        return controller.getObjectMapper().readDto(s,dtoClass);
    }

    public  <Dto> Dto deserialize(String s, JavaType dtoClass) throws IOException {
        return controller.getObjectMapper().readDto(s,dtoClass);
    }

    public  String serialize(Object o) throws JsonProcessingException {
        return controller.getObjectMapper().writeDto(o);
    }

}
