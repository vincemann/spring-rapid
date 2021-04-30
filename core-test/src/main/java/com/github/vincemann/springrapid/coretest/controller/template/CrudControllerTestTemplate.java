package com.github.vincemann.springrapid.coretest.controller.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.Getter;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Getter
public abstract class CrudControllerTestTemplate
        <C extends GenericCrudController<?, Id, ?, ?, ?>, Id extends Serializable>
            extends ControllerTestTemplate<C>
{

    public CrudControllerTestTemplate(C controller) {
        super(controller);
    }

    public abstract MockHttpServletRequestBuilder delete(Id id) throws Exception;

    public abstract MockHttpServletRequestBuilder find(Id id) throws Exception;

    public abstract MockHttpServletRequestBuilder update(String patchString, Id id) throws Exception;


    public  MockHttpServletRequestBuilder create(Object dto) throws Exception {
        return post(getCreateUrl())
                .content(serialize(dto))
                .contentType(getController().getCoreProperties().getController().getMediaType());
    }

    public  MockHttpServletRequestBuilder findAll() throws Exception {
        return get(getController().getFindAllUrl())/*.contentType(getContentType())*/;
    }

    public  String getCreateUrl() {
        return getController().getCreateUrl();
    }

    public  String getFindUrl() {
        return getController().getFindUrl();
    }

    public  String getDeleteUrl() {
        return getController().getDeleteUrl();
    }

    public  String getUpdateUrl() {
        return getController().getUpdateUrl();
    }

    public  String getFindAllUrl() {
        return getController().getFindAllUrl();
    }

}

