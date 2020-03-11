package io.github.vincemann.generic.crud.lib.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class InitializingTest{

    @Autowired
    private Optional<List<TestInitializable>> initializables;
    @Autowired
    private Optional<List<BeforeEachMethodInitializable>> beforeEachMethodInitializables;
    @Autowired
    private Optional<List<TestContextAware>> testContextAwareList;


    private boolean init = false;


    @BeforeEach
    public void setup() throws Exception{
        if(!init){
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
            init=true;
        }
        beforeEachMethodInitializables.ifPresent(l -> l.forEach(e -> {
            if(e.supports(this.getClass())){
                log.debug("calling init method of  bean : " +e);
                e.init();
            }
        }));
    }
}
