package com.github.vincemann.springrapid.coretest.controller.rapid;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;

import com.github.vincemann.springrapid.core.controller.RapidController;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import com.github.vincemann.springrapid.coretest.controller.AutoMockMvcControllerTest;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * Use this base class to perform integration tests of your {@link RapidController}.
 *
 * Offers basic crud methods to interact with controller and convenience methods to use Controllers {@link ObjectMapper} to
 * {@link this#serialize(Object)} and {@link this#deserialize(String, JavaType)} raw JSON Strings.
 *
 * @see AutoMockMvcControllerTest
 *
 * @param <S>  Service Type used by Controller
 * @param <E>  EntityType managed by Service
 * @param <Id> IdType of Entity
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractMvcRapidControllerTest
        <S extends CrudService<E,Id,?>
        ,E extends IdentifiableEntity<Id>,
        Id extends Serializable>
        extends AutoMockMvcControllerTest implements MvcRapidControllerTest<S,E,Id>
{
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    private DtoMappingContext dtoMappingContext;
    private RapidController<E, Id,S> controller;
    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    public void injectController(RapidController<E, Id,S> controller) {
        this.controller = controller;
    }


    @BeforeEach
    public void setup() throws Exception{
        super.setup();
        //user might want to inject own beans that are diff from controllers beans -> null checks
        if(dtoMappingContext ==null) {
            dtoMappingContext = getController().getDtoMappingContext();
        }
    }



}
