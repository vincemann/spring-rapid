package com.github.vincemann.springrapid.coretest.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import com.github.vincemann.springrapid.coretest.controller.automock.AutoMockControllerTest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * Use this base class to perform integration tests of your {@link GenericCrudController}.
 *
 * Offers basic crud methods to interact with controller and convenience methods to use Controllers {@link ObjectMapper} to
 * {@link this#serialize(Object)} and {@link this#deserialize(String, JavaType)} raw JSON Strings.
 *
 * @see AutoMockControllerTest
 *
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractCrudControllerTest<C extends GenericCrudController>
        extends AutoMockControllerTest
            implements MvcCrudControllerTest<C>
{
//    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    private DtoMappingContext dtoMappingContext;
    private C controller;
    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    public void injectController(C controller) {
        this.controller = controller;
    }


    @BeforeEach
    protected void setupDtoMappingContext() throws Exception{
        //user might want to inject own beans that are diff from controllers beans -> null checks
        if(dtoMappingContext ==null) {
            dtoMappingContext = getController().getDtoMappingContext();
        }
    }



}
