package com.github.vincemann.springrapid.coretest.controller;

import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractMvcTest implements InitializingBean
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
        assertThat("entity needs to be present in collection", entity.isPresent());
        return entity.get();
    }

    public <E extends IdAwareEntity<?>, E2 extends IdAwareEntity<?>> E assertCanFindInCollection(Collection<E> collection, E2 entity){
        Optional<E> filtered = collection.stream().filter(e -> e.getId().equals(entity.getId())).findFirst();
        assertThat("entity needs to be present in collection", filtered.isPresent());
        return filtered.get();
    }

    public MockMvc getMvc() {
        return mvc;
    }

    public WebApplicationContext getWac() {
        return wac;
    }

    @Autowired
    public void setWac(WebApplicationContext wac) {
        this.wac = wac;
    }
}
