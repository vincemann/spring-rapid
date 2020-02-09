package io.github.vincemann.generic.crud.lib.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class InitializingTest implements InitializingBean {

    private List<BeforeEachMethodInitializable> beforeEachMethodInitializables = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        initInitializableComponents();
        initTestAwareComponents();
    }

    @BeforeEach
    void callBeforeEachCallbacks(){
        beforeEachMethodInitializables.forEach(TestInitializable::init);
    }


    private void initInitializableComponents() throws IllegalAccessException {
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            if(TestInitializable.class.isAssignableFrom(declaredField.getType())){
                log.debug("found controller test aware field, with name : " + declaredField.getName() +", passing testObject for initialization");
                declaredField.setAccessible(true);
                TestInitializable initializable = (TestInitializable)declaredField.get(this);
                if(initializable!=null){
                    if(initializable instanceof BeforeEachMethodInitializable){
                        beforeEachMethodInitializables.add(((BeforeEachMethodInitializable) initializable));
                    }else {
                        initializable.init();
                    }
                }
            }
        }
    }

    private void initTestAwareComponents() throws IllegalAccessException {
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            if(TestAware.class.isAssignableFrom(declaredField.getType())){
                log.debug("found controller test aware field, with name : " + declaredField.getName() +", passing testObject for initialization");
                declaredField.setAccessible(true);
                TestAware testAware = (TestAware)declaredField.get(this);
                if(testAware!=null) {
                    testAware.setTest(this);
                }
            }
        }
    }
}
