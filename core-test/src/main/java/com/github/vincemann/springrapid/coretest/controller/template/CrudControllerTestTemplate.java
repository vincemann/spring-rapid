package com.github.vincemann.springrapid.coretest.controller.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.coretest.controller.UrlWebExtension;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
        extends MvcControllerTestTemplate<C> {

    @Autowired
    private ApplicationContext applicationContext;

    public MockHttpServletRequestBuilder delete(Serializable id) throws Exception {
        return MockMvcRequestBuilders.delete(controller.getDeleteUrl())
                /*.contentType(getContentType())*/
                .param("id",id.toString());
    }

    public MockHttpServletRequestBuilder find(Serializable id) throws Exception {
        return get(controller.getFindUrl())
                /*.contentType(getContentType())*/
                .param("id",id.toString());
    }

    public MockHttpServletRequestBuilder update(String patchString,Serializable id) throws Exception {
//        String fullUpdateQueryParam = getController().getFullUpdateQueryParam();
        return put(controller.getUpdateUrl())
                .param("id",id.toString())
                .content(patchString)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }


    public  MockHttpServletRequestBuilder create(Object dto) throws Exception {
        return post(controller.getCreateUrl())
                .content(serialize(dto))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }


    public <Dto> Dto performDs2xx(RequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
        return performDsWithStatus(requestBuilder,status().is2xxSuccessful(),dtoClass);
    }

    public <Dto> Dto performDsWithStatus(RequestBuilder requestBuilder, ResultMatcher status, Class<Dto> dtoClass) throws Exception {
        return deserialize(getMvc().perform(requestBuilder)
                .andExpect(status)
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }

    public ResultActions perform2xx(RequestBuilder requestBuilder) throws Exception {
        return getMvc().perform(requestBuilder).andExpect(status().is2xxSuccessful());
    }

    public ResultActions perform(RequestBuilder requestBuilder) throws Exception {
        return getMvc().perform(requestBuilder);
    }

    public  MockHttpServletRequestBuilder findAll() throws Exception {
        return get(getController().getFindAllUrl())/*.contentType(getContentType())*/;
    }

    public  MockHttpServletRequestBuilder findSome(Set<String> ids) throws Exception {
        return post(controller.getFindSomeUrl())
                .content(getController().getJsonMapper().writeDto(ids))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }


    public  MockHttpServletRequestBuilder findAll(UrlWebExtension... extensions) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(controller.getFindAllUrl());
        RapidTestUtil.addUrlExtensionsToRequest(applicationContext,requestBuilder,extensions);
        return requestBuilder;
    }

    public <E extends IdentifiableEntity<?>> E mapToEntity(Object dto) throws BadEntityException, EntityNotFoundException {
        return (E) getController().getDtoMapper().mapToEntity(dto, controller.getEntityClass());
    }

    public  <Dto> Dto deserialize(String s, Class<Dto> dtoClass) throws IOException {
        return getController().getJsonMapper().readDto(s, dtoClass);
    }

    public  <Dto> Dto deserialize(String s, TypeReference<?> dtoClass) throws IOException {
        return getController().getJsonMapper().readDto(s, dtoClass);
    }

    public  <Dto> List<Dto> deserializeToList(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(List.class, dtoClass);
        return deserialize(s, setType);
    }

    public  <Dto> Dto deserialize(String s, JavaType dtoClass) throws IOException {
        return getController().getJsonMapper().readDto(s, dtoClass);
    }

    public  String serialize(Object o) throws JsonProcessingException {
        return getController().getJsonMapper().writeDto(o);
    }


    public  <Dto> Dto readDto(MvcResult mvcResult, Class<Dto> dtoClass) throws Exception {
        return deserialize(mvcResult.getResponse().getContentAsString(), dtoClass);
    }
}
