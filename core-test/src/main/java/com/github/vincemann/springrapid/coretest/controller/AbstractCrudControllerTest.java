package com.github.vincemann.springrapid.coretest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.coretest.InitializingTest;
import com.github.vincemann.springrapid.coretest.controller.automock.AbstractAutoMockCrudControllerTest;
import com.github.vincemann.springrapid.coretest.controller.integration.AbstractIntegrationControllerTest;
import com.github.vincemann.springrapid.coretest.controller.template.AbstractControllerTestTemplate;
import com.github.vincemann.springrapid.coretest.controller.template.AbstractCrudControllerTestTemplate;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

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
    private C controller;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private WebApplicationContext wac;

    // use TestTemplate as member and not inherit so in one test multiple TestTemplates can be used and @Autowired in
    // with inheritence @AutoConfigureMockMvc ect. does not make this possible
    private T testTemplate;


    @Override
    public void afterPropertiesSet() throws Exception {
        this.getTestTemplate().setController(controller);
        DefaultMockMvcBuilder mvcBuilder = createMvcBuilder();
        this.contentType = MediaType.valueOf(controller.getCoreProperties().getController().getMediaType());
        mvc = mvcBuilder.build();
        initTestTemplatesMvc();
    }


    protected void initTestTemplatesMvc(){
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


    public <Dto> Dto perform2xx(RequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
       return deserialize(getMvc().perform(requestBuilder)
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }




    // CONVENIENCE METHODS SO USER DOES NOT HAVE TO CALL testTemplate.foo(...)

    public MockHttpServletRequestBuilder delete(Object id) throws Exception{
        return testTemplate.delete(id.toString());
    }

    public MockHttpServletRequestBuilder find(Object id) throws Exception{
        return testTemplate.find(id.toString());
    }

    public MockHttpServletRequestBuilder update(String patchString, Object id) throws Exception{
        return testTemplate.update(patchString,id.toString());
    }


    public <E extends IdentifiableEntity<?>> E mapToEntity(Object dto) throws BadEntityException, EntityNotFoundException {
        return (E) testTemplate.mapToEntity(dto);
    }

    public  MockHttpServletRequestBuilder create(Object dto) throws Exception {
        return testTemplate.create(dto);
    }

    public  MockHttpServletRequestBuilder findAll() throws Exception {
        return testTemplate.findAll();
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

    public  String serialize(Object o) throws JsonProcessingException {
        return testTemplate.serialize(o);
    }

    public  <Dto> Dto deserialize(String s, Class<Dto> dtoClass) throws IOException {
        return (Dto) testTemplate.deserialize(s,dtoClass);
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
