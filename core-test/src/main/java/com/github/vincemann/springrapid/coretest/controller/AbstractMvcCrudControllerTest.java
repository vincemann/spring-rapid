package com.github.vincemann.springrapid.coretest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.coretest.InitializingTest;
import lombok.Getter;
import lombok.Setter;
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
        <C extends GenericCrudController<?, Id, ?, ?, ?>, Id extends Serializable>
            extends InitializingTest
                implements MvcCrudControllerTest<C,Id>
{

    
    private MockMvc mockMvc;
    private DefaultMockMvcBuilder mockMvcBuilder;
    private MediaType contentType;
    private CoreProperties coreProperties;
    private C controller;
    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    protected void setupMvc(WebApplicationContext wac) {
        this.contentType = MediaType.valueOf(coreProperties.getController().getMediaType());
        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(wac)
                //user has to check himself if he wants to
//                .alwaysExpect(content().contentType(getContentType()))
                .alwaysDo(print());
        mockMvc = mockMvcBuilder.build();
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
