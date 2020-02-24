package io.github.vincemann.generic.crud.lib.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public abstract class InitializingTest implements InitializingBean {

    @Autowired
    private Optional<List<TestInitializable>> initializables;
    @Autowired
    private Optional<List<BeforeEachMethodInitializable>> beforeEachMethodInitializables;
    @Autowired
    private Optional<List<TestContextAware>> testContextAwareList;



    @Override
    public void afterPropertiesSet() throws Exception {
        //initComponents();
        //init all components in spring container
        initializables.ifPresent(l -> l.forEach(e -> {
            if(e.supports(this.getClass())){
                log.debug("calling init method of  bean : " +e);
                e.init();
            }
        }));
        testContextAwareList.ifPresent(l -> l.forEach(a -> {
            if(a.supports(this.getClass())) {
                log.debug("giving test context to bean : " +a);
                a.setTestContext(this);
            }
        }));
    }

    @BeforeEach
    void callBeforeEachCallbacks(){
        beforeEachMethodInitializables.ifPresent(l -> l.forEach(e -> {
            if(e.supports(this.getClass())){
                log.debug("calling init method of  bean : " +e);
                e.init();
            }
        }));
    }
}
