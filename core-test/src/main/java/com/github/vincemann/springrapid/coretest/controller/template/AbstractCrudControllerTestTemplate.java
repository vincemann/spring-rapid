package com.github.vincemann.springrapid.coretest.controller.template;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.service.JPQLEntityFilter;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Getter
public abstract class AbstractCrudControllerTestTemplate
        <C extends GenericCrudController>
            extends AbstractControllerTestTemplate<C>
{


    public MockHttpServletRequestBuilder delete(String id) throws Exception {
        return MockMvcRequestBuilders.delete(getDeleteUrl())
                /*.contentType(getContentType())*/
                .param("id",id);
    }

    public MockHttpServletRequestBuilder find(String id) throws Exception {
        return get(getFindUrl())
                /*.contentType(getContentType())*/
                .param("id",id);
    }

    public MockHttpServletRequestBuilder update(String patchString,String id) throws Exception {
//        String fullUpdateQueryParam = getController().getFullUpdateQueryParam();
        return put(getUpdateUrl())
                .param("id",id)
                .content(patchString)
                .contentType(getController().getCoreProperties().getController().getMediaType());
    }


    public  MockHttpServletRequestBuilder create(Object dto) throws Exception {
        return post(getCreateUrl())
                .content(serialize(dto))
                .contentType(getController().getCoreProperties().getController().getMediaType());
    }


    // todo duplicated in TestTemplate

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

    public  MockHttpServletRequestBuilder findAll(String jpqlFilters) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(getController().getFindAllUrl());
        requestBuilder.param("jpql-filter",jpqlFilters);
        return requestBuilder;
    }

    public  MockHttpServletRequestBuilder findAll(String jpqlFilters, String memoryFilters) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(getController().getFindAllUrl());
        if (jpqlFilters != null){
            requestBuilder.param("jpql-filter",jpqlFilters);
        }
        if (memoryFilters != null){
            requestBuilder.param("filter",memoryFilters);
        }
        return requestBuilder;
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

    public String getFindSomeUrl(){
        return getController().getFindSomeUrl();
    }

}

