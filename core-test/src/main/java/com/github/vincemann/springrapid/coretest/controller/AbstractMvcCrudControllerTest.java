package com.github.vincemann.springrapid.coretest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.coretest.InitializingTest;
import com.github.vincemann.springrapid.coretest.controller.integration.IntegrationControllerTest;
import com.github.vincemann.springrapid.coretest.controller.template.CrudControllerTestTemplate;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.Serializable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * Base class for tests of {@link GenericCrudController}.
 * Use either implementations of
 *  {@link com.github.vincemann.springrapid.coretest.controller.automock.AutoMockControllerTest}
 * or
 *  {@link IntegrationControllerTest}
 * to test your {@link GenericCrudController}s.
 *
 * Offers basic crud methods to interact with controller and convenience methods to use Controllers {@link com.fasterxml.jackson.databind.ObjectMapper} to
 * {@link this#serialize(Object)} and {@link this#deserialize(String, JavaType)} raw JSON Strings.
 */
@Getter
@AutoConfigureMockMvc
@SpringBootTest
public abstract class AbstractMvcCrudControllerTest
        <C extends GenericCrudController<?, Id, ?, ?, ?>, Id extends Serializable,T extends CrudControllerTestTemplate<C,Id>>
            extends InitializingTest
{


    private MockMvc mockMvc;
    private DefaultMockMvcBuilder mockMvcBuilder;
    private MediaType contentType;
    private CoreProperties coreProperties;
    private C controller;
    @Autowired
    private ApplicationContext applicationContext;

    // use TestTemplate as member and not inherit so in one test multiple TestTemplates can be used and @Autowired in
    // with inheritence @AutoConfigureMockMvc ect. does not make this possible
    private T testTemplate;



    @BeforeEach
    protected void setupMvc(WebApplicationContext wac) {
        this.contentType = MediaType.valueOf(coreProperties.getController().getMediaType());
        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac)
                //user has to check himself if he wants to
//                .alwaysExpect(content().contentType(getContentType()))
                .alwaysDo(print());
        mockMvc = mockMvcBuilder.build();
    }

    @BeforeEach
    protected void setupTestTemplate(){
        testTemplate= createTestTemplate();
    }

    public abstract T createTestTemplate();


    // CONVENIENCE METHODS SO USER DOES NOT HAVE TO CALL testTemplate.foo(...)

    public MockHttpServletRequestBuilder delete(Id id) throws Exception{
        return testTemplate.delete(id);
    }

    public MockHttpServletRequestBuilder find(Id id) throws Exception{
        return testTemplate.find(id);
    }

    public MockHttpServletRequestBuilder update(String patchString, Id id) throws Exception{
        return testTemplate.update(patchString,id);
    }


    public <E extends IdentifiableEntity<?>> E mapToEntity(Object dto) throws BadEntityException, EntityNotFoundException {
        return testTemplate.mapToEntity(dto);
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
        return testTemplate.deserialize(s,dtoClass);
    }

    public  <Dto> Dto deserialize(String s, TypeReference<?> dtoClass) throws IOException {
        return testTemplate.deserialize(s,dtoClass);
    }

    public  <Dto> Dto deserialize(String s, JavaType dtoClass) throws IOException {
        return testTemplate.deserialize(s,dtoClass);
    }

    public  <Dto> Dto readDto(MvcResult mvcResult, Class<Dto> dtoClass) throws Exception {
        return testTemplate.readDto(mvcResult,dtoClass);
    }


    @Autowired
    public void injectController(C controller) {
        this.controller = controller;
    }

    @Autowired
    public void injectCoreProperties(CoreProperties coreProperties) {
        this.coreProperties = coreProperties;
    }
}
