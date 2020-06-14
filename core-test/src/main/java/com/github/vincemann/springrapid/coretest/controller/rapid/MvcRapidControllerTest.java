package com.github.vincemann.springrapid.coretest.controller.rapid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import com.github.vincemann.springrapid.core.controller.RapidController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public interface MvcRapidControllerTest<S extends CrudService<E,Id,?>
        ,E extends IdentifiableEntity<Id>,
        Id extends Serializable> {

    public RapidController<E, Id,S> getController();


    public default E mapToEntity(IdentifiableEntity<Id> dto) throws BadEntityException, EntityNotFoundException {
        return (E) getController().getDtoMapper().mapToEntity(dto,getController().getEntityClass());
    }

    public default MockHttpServletRequestBuilder create(Object dto) throws Exception {
        return post(getCreateUrl())
                .content(serialize(dto))
                .contentType(getController().getMediaType());
    }

    public default MockHttpServletRequestBuilder findAll() throws Exception {
        return get(getController().getFindAllUrl())/*.contentType(getContentType())*/;
    }

    public default String getCreateUrl(){
        return getController().getCreateUrl();
    }

    public default String getFindUrl(){
        return getController().getFindUrl();
    }
    public default String getDeleteUrl(){
        return getController().getDeleteUrl();
    }
    public default String getUpdateUrl(){
        return getController().getUpdateUrl();
    }

    public default String getFindAllUrl(){
        return getController().getFindAllUrl();
    }

    public default String serialize(Object o) throws JsonProcessingException {
        return getController().getJsonMapper().writeValueAsString(o);
    }

    public default <Dto> Dto deserialize(String s,Class<Dto> dtoClass) throws IOException {
        return getController().getJsonMapper().readValue(s,dtoClass);
    }

    public default <Dto> Dto deserialize(String s, TypeReference<?> dtoClass) throws IOException {
        return (Dto) getController().getJsonMapper().readValue(s,dtoClass);
    }

    public default <Dto> Dto deserialize(String s, JavaType dtoClass) throws IOException {
        return getController().getJsonMapper().readValue(s,dtoClass);
    }


    public default <Dto> Dto readDto(MvcResult mvcResult, Class<Dto> dtoClass) throws Exception{
        return deserialize(mvcResult.getResponse().getContentAsString(),dtoClass);
    }
}
