package com.github.vincemann.springrapid.coretest.controller.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.coretest.controller.MvcAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * helper class for mock mvc integration tests.
 * Represents a controller in a test.
 * Example:
 *
 * @Autowired
 * PetControllerTestTemplate petController;
 *
 *
 * @Test
 * void test(){
 *      ReadPetDto response = petController.create(new CreatePetDto());
 * }
 * @param <C> represented controller type
 */
public abstract class MvcControllerTestTemplate<C> implements MvcAware {
    protected C controller;
    protected MockMvc mvc;
    private ObjectMapper objectMapper;

    @Autowired
    public void setController(C controller) {
        this.controller = controller;
    }

    @Override
    public void setMvc(MockMvc mvc) {
        this.mvc = mvc;
    }

    public ResultActions perform(RequestBuilder requestBuilder) throws Exception {
        return mvc.perform(requestBuilder);
    }

    public ResultActions perform2xx(RequestBuilder requestBuilder) throws Exception {
        return mvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());
    }

    // MVC PERFORM


    /**
     * perform, expect 2xx and deserialize result to dtoClass
     */
    public <Dto> Dto perform2xxAndDeserialize(MockHttpServletRequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
        return performAndDeserialize(requestBuilder,status().is2xxSuccessful(),dtoClass);
    }


    public <Dto> Dto performAndDeserialize(MockHttpServletRequestBuilder requestBuilder, ResultMatcher status, Class<Dto> dtoClass) throws Exception {
        return jsonToDto(mvc.perform(requestBuilder)
                .andExpect(status)
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }

    public <Dto> Set<Dto> perform2xxAndDeserializeToSet(MockHttpServletRequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
        return jsonToSet(mvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }
    public <Dto> List<Dto> perform2xxAndDeserializeToList(MockHttpServletRequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
        return jsonToList(mvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }

    public static MockHttpServletRequestBuilder withToken(MockHttpServletRequestBuilder builder, String token){
        return builder.header(HttpHeaders.AUTHORIZATION,token);
    }


    // SERIALIZATION


    public  <Dto> Dto jsonToDto(String s, Class<Dto> dtoClass) throws IOException {
        return objectMapper.readValue(s,dtoClass);
    }

    public  <Dto> Set<Dto> jsonToSet(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = objectMapper
                .getTypeFactory().constructCollectionType(Set.class, dtoClass);
        return jsonToDto(s, setType);
    }

    public  <Dto> List<Dto> jsonToList(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = objectMapper
                .getTypeFactory().constructCollectionType(List.class, dtoClass);
        return jsonToDto(s, setType);
    }

    public  <Dto> Dto jsonToDto(String s, TypeReference<Dto> dtoClass) throws IOException {
        return objectMapper.readValue(s,dtoClass);
    }

    public  <Dto> Dto jsonToDto(String s, JavaType dtoClass) throws IOException {
        return objectMapper.readValue(s,dtoClass);
    }

    public  String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected C getController() {
        return controller;
    }

    protected MockMvc getMvc() {
        return mvc;
    }
}
