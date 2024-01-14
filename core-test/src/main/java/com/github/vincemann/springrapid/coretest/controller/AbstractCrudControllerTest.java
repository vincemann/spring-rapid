package com.github.vincemann.springrapid.coretest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.filter.ArgAware;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.coretest.InitializingTest;
import com.github.vincemann.springrapid.coretest.controller.automock.AbstractAutoMockCrudControllerTest;
import com.github.vincemann.springrapid.coretest.controller.integration.AbstractIntegrationControllerTest;
import com.github.vincemann.springrapid.coretest.controller.template.AbstractControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.controller.template.AbstractCrudControllerTestTemplate;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Base class for tests of {@link GenericCrudController}.
 * Use either implementations of
 *  {@link AbstractAutoMockCrudControllerTest}
 * or
 *  {@link AbstractIntegrationControllerTest}
 * to test your {@link GenericCrudController}s.
 *
 * Offers basic crud methods to interact with controller and convenience methods to use Controllers {@link com.fasterxml.jackson.databind.ObjectMapper} to
 * {@link this#serialize(Object)} and {@link this#deserialize(String, JavaType)} raw JSON Strings.
 */
@Getter
@AutoConfigureMockMvc
@SpringBootTest
public abstract class AbstractCrudControllerTest
        <C extends GenericCrudController , T extends AbstractCrudControllerTestTemplate>
            extends InitializingTest implements InitializingBean
{


    protected MockMvc mvc;
    private MediaType contentType;
    protected C controller;
    @Autowired
    private WebApplicationContext wac;

    // use TestTemplate as member and not inherit so in one test multiple TestTemplates can be used and @Autowired in
    // with inheritence @AutoConfigureMockMvc ect. does not make this possible
    protected T testTemplate;

//    @Override
//    protected DefaultMockMvcBuilder createMvcBuilder() {
//        DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
//        mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
//        return mvcBuilder;
//    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.getTestTemplate().setController(controller);
        DefaultMockMvcBuilder mvcBuilder = createMvcBuilder();
        this.contentType = MediaType.valueOf(controller.getCoreProperties().getController().getMediaType());
        mvc = mvcBuilder.build();
        setTestTemplatesMvc();
    }



    protected void setTestTemplatesMvc(){
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            Class<?> fieldType = field.getType();
            if (AbstractControllerTestTemplate.class.isAssignableFrom(fieldType)){
                field.setAccessible(true);
                AbstractControllerTestTemplate testTemplate = (AbstractControllerTestTemplate) field.get(this);
                testTemplate.setMvc(mvc);
            }
        });
    }

    @Autowired
    public void injectTestTemplate(T testTemplate){
        this.testTemplate=testTemplate;
    }

    protected DefaultMockMvcBuilder createMvcBuilder() {
        return MockMvcBuilders.webAppContextSetup(wac)
                .alwaysDo(print());
    }


    // todo duplicated in TestTemplate
    public ResultActions perform2xx(RequestBuilder requestBuilder) throws Exception {
        return getMvc().perform(requestBuilder).andExpect(status().is2xxSuccessful());
    }

    public ResultActions perform(RequestBuilder requestBuilder) throws Exception {
        return getMvc().perform(requestBuilder);
    }


    /**
     * perform and deserialize result to dtoClass
     */
    public <Dto> Dto performDs2xx(RequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
       return performDsWithStatus(requestBuilder,status().is2xxSuccessful(),dtoClass);
    }

    public <Dto> Dto performDsWithStatus(RequestBuilder requestBuilder, ResultMatcher status, Class<Dto> dtoClass) throws Exception {
        return deserialize(getMvc().perform(requestBuilder)
                .andExpect(status)
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }




    // CONVENIENCE METHODS SO USER DOES NOT HAVE TO CALL testTemplate.foo(...)

    public MockHttpServletRequestBuilder delete(Object id) throws Exception{
        Assert.notNull(id);
        return testTemplate.delete(id.toString());
    }

    public MockHttpServletRequestBuilder find(Object id) throws Exception{
        Assert.notNull(id);
        return testTemplate.find(id.toString());
    }

    public MockHttpServletRequestBuilder update(String patchString, Object id) throws Exception{
        Assert.notNull(id);
        Assert.notNull(patchString);
        return testTemplate.update(patchString,id.toString());
    }


    public <E extends IdentifiableEntity<?>> E mapToEntity(Object dto) throws BadEntityException, EntityNotFoundException {
        Assert.notNull(dto);
        return (E) testTemplate.mapToEntity(dto);
    }

    public  MockHttpServletRequestBuilder create(Object dto) throws Exception {
        Assert.notNull(dto);
        return testTemplate.create(dto);
    }

    public  MockHttpServletRequestBuilder findAll() throws Exception {
        return testTemplate.findAll();
    }

    public  MockHttpServletRequestBuilder findAll(UrlExtension... extensions) throws Exception {
        return testTemplate.findAll(extensions);
    }

    public  String getCreateUrl() {
        return testTemplate.getCreateUrl();
    }

    public  String getFindUrl() {
        return testTemplate.getFindUrl();
    }

    public  String getDeleteUrl() {
        return testTemplate.getDeleteUrl();
    }

    public  String getUpdateUrl() {
        return testTemplate.getUpdateUrl();
    }

    public  String getFindAllUrl() {
        return testTemplate.getFindAllUrl();
    }

    public String getFindSomeUrl(){
        return testTemplate.getFindSomeUrl();
    }

    public  String serialize(Object o) throws JsonProcessingException {
        return testTemplate.serialize(o);
    }

    public  <Dto> Dto deserialize(String s, Class<Dto> dtoClass) throws IOException {
        return (Dto) testTemplate.deserialize(s,dtoClass);
    }

    public  <Dto> Set<Dto> deserializeToSet(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = getController().getJsonMapper().getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, dtoClass);
        return deserialize(s, setType);
    }

    public <E> E findInCollection(Collection<E> collection, Predicate<E> predicate){
        Optional<E> entity = collection.stream().filter(predicate::test).findFirst();
        Assertions.assertTrue(entity.isPresent(),"could not find entity in collection");
        return entity.get();
    }

    public <E extends IdentifiableEntity<?>, E2 extends IdentifiableEntity<?>> E findInCollection(Collection<E> collection, E2 entity){
        Optional<E> filtered = collection.stream().filter(e -> e.getId().equals(entity.getId())).findFirst();
        Assertions.assertTrue(filtered.isPresent(),"could not find entity in collection");
        return filtered.get();
    }

    public  <Dto> Dto deserialize(String s, TypeReference<?> dtoClass) throws IOException {
        return (Dto) testTemplate.deserialize(s,dtoClass);
    }

    public  <Dto> Dto deserialize(String s, JavaType dtoClass) throws IOException {
        return (Dto) testTemplate.deserialize(s,dtoClass);
    }

    public  <Dto> Dto readDto(MvcResult mvcResult, Class<Dto> dtoClass) throws Exception {
        return (Dto) testTemplate.readDto(mvcResult,dtoClass);
    }


    @Autowired
    public void injectController(C controller) {
        this.controller = controller;
    }
}
