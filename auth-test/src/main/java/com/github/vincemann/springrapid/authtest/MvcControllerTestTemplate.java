package com.github.vincemann.springrapid.authtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
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
 *      ReadPetDto response = mvc.perform(petController.create(new CreatePetDto())
 *                                              .header(HttpHeaders.Authorization,myToken))
 *                                  .andExpect(status().is2xxSuccessful());
 * }
 * @param <C> represented controller type
 */
public abstract class MvcControllerTestTemplate<C> {
    protected C controller;
    protected MockMvc mvc;
    private ObjectMapper objectMapper;

    @Autowired
    public void setController(C controller) {
        this.controller = controller;
    }

    @Autowired
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
        return objectMapper.readValue(s,dtoClass);
    }

    public  <Dto> Set<Dto> deserializeToSet(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = objectMapper
                .getTypeFactory().constructCollectionType(Set.class, dtoClass);
        return deserialize(s, setType);
    }

    public  <Dto> List<Dto> deserializeToList(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = objectMapper
                .getTypeFactory().constructCollectionType(List.class, dtoClass);
        return deserialize(s, setType);
    }

    public  <Dto> Dto deserialize(String s, TypeReference<Dto> dtoClass) throws IOException {
        return objectMapper.readValue(s,dtoClass);
    }

    public  <Dto> Dto deserialize(String s, JavaType dtoClass) throws IOException {
        return objectMapper.readValue(s,dtoClass);
    }

    public  String serialize(Object o) throws JsonProcessingException {
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
