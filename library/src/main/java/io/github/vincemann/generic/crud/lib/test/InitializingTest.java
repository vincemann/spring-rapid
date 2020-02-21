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
        initializables.ifPresent(l -> l.forEach(TestInitializable::init));
        testContextAwareList.ifPresent(l -> l.forEach(a -> {
            if(a.supports(this.getClass())) {
                log.debug("giving test context to bean : " +a);
                a.setTestContext(this);
            }
        }));
    }

    @BeforeEach
    void callBeforeEachCallbacks(){
        beforeEachMethodInitializables.ifPresent(l -> l.forEach(TestInitializable::init));
    }


//    private void initComponents() throws IllegalAccessException {
//        for (Field declaredField : getAllFields(new LinkedList<>(),this.getClass())) {
//            declaredField.setAccessible(true);
//            Object member = declaredField.get(this);
//            if(member!=null) {
//                if (member instanceof TestInitializable) {
//                    log.debug("found controller test aware member of type : " + member.getClass().getSimpleName() + ", passing testObject for initialization");
//                    if(member instanceof BeforeEachMethodInitializable){
//                        beforeEachMethodInitializables.add(((BeforeEachMethodInitializable) member));
//                    }else {
//                        ((TestInitializable) member).init();
//                    }
//                }
//                if(member instanceof TestContextAware){
//                    log.debug("found controller test aware member, of type : " + member.getClass().getSimpleName() +", passing testObject for initialization");
//                    ((TestContextAware) member).setTestContext(this);
//                }
//            }
//        }
//    }
//
//    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
//        fields.addAll(Arrays.asList(type.getDeclaredFields()));
//
//        if (type.getSuperclass() != null) {
//            getAllFields(fields, type.getSuperclass());
//        }
//
//        return fields;
//    }

}
