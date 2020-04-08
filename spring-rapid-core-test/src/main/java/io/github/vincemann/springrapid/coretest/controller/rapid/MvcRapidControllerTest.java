package io.github.vincemann.springrapid.coretest.controller.rapid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import io.github.vincemann.springrapid.coretest.controller.MvcControllerTest;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * Use this base class to test your Controllers.
 * Service is tested in isolation, that's why it should be mocked here and only the correct interaction
 * with the service is getting tested. -> service layer not loaded -> web only context is loaded
 *
 * Main Goal of this test, is to test the correct setup of the web-layer and that the webcomponents
 * work together as expected.
 * Each component is tested heavily in isolation though, which means, that a few simple test cases here are sufficient.
 *
 * @param <S> Service Type
 * @param <E> Entity managed by Service Type
 * @param <Id> Id Type of Entity
 */
@Getter
@Setter
@Slf4j
public abstract class MvcRapidControllerTest
        <S extends CrudService<E,Id,? extends CrudRepository<E,Id>>
        ,E extends IdentifiableEntity<Id>,
        Id extends Serializable>
        extends MvcControllerTest
{
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private DtoMappingContext dtoMappingContext;
    private String url;
    private RapidController<E, Id> controller;


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    public void injectController(RapidController<E, Id> controller) {
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


    public E mapToEntity(IdentifiableEntity<Id> dto) throws DtoMappingException {
        return getController().getDtoMapper().mapToEntity(dto,getEntityClass());
    }

    public MockHttpServletRequestBuilder create(IdentifiableEntity<Id> dto) throws Exception {
        return post(getCreateUrl())
                        .content(serialize(dto))
                        .contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    public MockHttpServletRequestBuilder update(IdentifiableEntity<Id> updateDto, Boolean full) throws Exception {
        String fullUpdateQueryParam = getController().getFullUpdateQueryParam();

        return put(getUpdateUrl()+"?"+fullUpdateQueryParam+"="+full.toString())
                .content(serialize(updateDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    public MockHttpServletRequestBuilder fullUpdate(IdentifiableEntity<Id> updateDto) throws Exception {
        return update(updateDto,Boolean.TRUE);
    }

    public MockHttpServletRequestBuilder partialUpdate(IdentifiableEntity<Id> updateDto) throws Exception {
        return update(updateDto,Boolean.FALSE);
    }

    public MockHttpServletRequestBuilder findAll() throws Exception {
        return get(getController().getFindAllUrl());
    }

    public String getCreateUrl(){
        return getController().getCreateUrl();
    }

    public String getFindUrl(){
        return getController().getFindUrl();
    }
    public String getDeleteUrl(){
        return getController().getDeleteUrl();
    }
    public String getUpdateUrl(){
        return getController().getUpdateUrl();
    }

    public String getFindAllUrl(){
        return getController().getFindAllUrl();
    }

    public String serialize(Object o) throws JsonProcessingException {
        return getController().getJsonMapper().writeValueAsString(o);
    }

    public <Dto> Dto deserialize(String s,Class<Dto> dtoClass) throws IOException {
        return getController().getJsonMapper().readValue(s,dtoClass);
    }

    public <Dto> Dto deserialize(String s, TypeReference<?> dtoClass) throws IOException {
        return (Dto) getController().getJsonMapper().readValue(s,dtoClass);
    }

    public <Dto> Dto deserialize(String s, JavaType dtoClass) throws IOException {
        return getController().getJsonMapper().readValue(s,dtoClass);
    }

    public <C extends RapidController<E,Id>> C getController(){
        return (C) controller;
    }


    public <Dto extends IdentifiableEntity<Id>> Dto readDto(MvcResult mvcResult, Class<Dto> dtoClass) throws Exception{
        return deserialize(mvcResult.getResponse().getContentAsString(),dtoClass);
    }


}
