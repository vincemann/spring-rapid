package com.github.vincemann.springrapid.coretest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.coretest.InitializingTest;
import com.github.vincemann.springrapid.coretest.MvcAware;
import com.github.vincemann.springrapid.coretest.JsonHelper;
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


@Getter
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractMvcTest extends InitializingTest implements InitializingBean
{


    protected MockMvc mvc;
    private WebApplicationContext wac;


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


    @Autowired
    public void setWac(WebApplicationContext wac) {
        this.wac = wac;
    }
}
