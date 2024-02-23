package com.github.vincemann.springrapid.coretest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.vincemann.springrapid.core.controller.json.JsonMapper;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.coretest.InitializingTest;
import com.github.vincemann.springrapid.coretest.MvcAware;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Getter
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractMvcTest extends InitializingTest implements InitializingBean
{


    protected MockMvc mvc;
    private WebApplicationContext wac;
    protected JsonMapper jsonMapper;


    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMockMvcBuilder mvcBuilder = createMvcBuilder();
        mvc = mvcBuilder.build();
        injectMvcIntoFields();
    }



    // iterates over all mvc aware fields and sets mvc
    protected void injectMvcIntoFields(){
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            Class<?> fieldType = field.getType();
            if (MvcAware.class.isAssignableFrom(fieldType)){
                field.setAccessible(true);
                MvcAware testTemplate = (MvcAware) field.get(this);
                testTemplate.setMvc(mvc);
            }
        });
    }


    protected DefaultMockMvcBuilder createMvcBuilder() {
        return MockMvcBuilders.webAppContextSetup(wac)
                .alwaysDo(print());
    }


    public ResultActions perform2xx(RequestBuilder requestBuilder) throws Exception {
        return getMvc().perform(requestBuilder).andExpect(status().is2xxSuccessful());
    }

    public ResultActions perform(RequestBuilder requestBuilder) throws Exception {
        return getMvc().perform(requestBuilder);
    }


    /**
     * perform, expect 2xx and deserialize result to dtoClass
     */
    public <Dto> Dto performDs2xx(RequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
       return performDsWithStatus(requestBuilder,status().is2xxSuccessful(),dtoClass);
    }

    public <Dto> Set<Dto> performDs2xxSet(RequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
        return deserializeToSet(getMvc().perform(requestBuilder)
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }
    public <Dto> List<Dto> performDs2xxList(RequestBuilder requestBuilder, Class<Dto> dtoClass) throws Exception {
        return deserializeToList(getMvc().perform(requestBuilder)
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }


    public <Dto> Dto performDsWithStatus(RequestBuilder requestBuilder, ResultMatcher status, Class<Dto> dtoClass) throws Exception {
        return deserialize(getMvc().perform(requestBuilder)
                .andExpect(status)
                .andReturn().getResponse().getContentAsString(),dtoClass);
    }


    public  <Dto> Dto deserialize(String s, Class<Dto> dtoClass) throws IOException {
        return (Dto) jsonMapper.readDto(s,dtoClass);
    }

    public  <Dto> Set<Dto> deserializeToSet(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = jsonMapper.getObjectMapper()
                .getTypeFactory().constructCollectionType(Set.class, dtoClass);
        return deserialize(s, setType);
    }

    public  <Dto> List<Dto> deserializeToList(String s, Class<Dto> dtoClass) throws IOException {
        CollectionType setType = jsonMapper.getObjectMapper()
                .getTypeFactory().constructCollectionType(List.class, dtoClass);
        return deserialize(s, setType);
    }

    public <E> E assertCanFindInCollection(Collection<E> collection, Predicate<E> predicate){
        Optional<E> entity = collection.stream().filter(predicate::test).findFirst();
        Assertions.assertTrue(entity.isPresent(),"could not find entity in collection");
        return entity.get();
    }

    public <E extends IdentifiableEntity<?>, E2 extends IdentifiableEntity<?>> E assertCanFindInCollection(Collection<E> collection, E2 entity){
        Optional<E> filtered = collection.stream().filter(e -> e.getId().equals(entity.getId())).findFirst();
        Assertions.assertTrue(filtered.isPresent(),"could not find entity in collection");
        return filtered.get();
    }

    public  <Dto> Dto deserialize(String s, TypeReference<?> dtoClass) throws IOException {
        return (Dto) jsonMapper.readDto(s,dtoClass);
    }

    public  <Dto> Dto deserialize(String s, JavaType dtoClass) throws IOException {
        return (Dto) jsonMapper.readDto(s,dtoClass);
    }

    @Autowired
    public void setJsonMapper(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Autowired
    public void setWac(WebApplicationContext wac) {
        this.wac = wac;
    }
}
