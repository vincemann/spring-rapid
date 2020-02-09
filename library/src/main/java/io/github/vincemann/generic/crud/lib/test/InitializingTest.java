package io.github.vincemann.generic.crud.lib.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public abstract class InitializingTest implements InitializingBean {

    private List<BeforeEachMethodInitializable> beforeEachMethodInitializables = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        initComponents();
    }

    @BeforeEach
    void callBeforeEachCallbacks(){
        beforeEachMethodInitializables.forEach(TestInitializable::init);
    }


    private void initComponents() throws IllegalAccessException {
        for (Field declaredField : getAllFields(new LinkedList<>(),this.getClass())) {
            declaredField.setAccessible(true);
            Object member = declaredField.get(this);
            if(member!=null) {
                if (member instanceof TestInitializable) {
                    log.debug("found controller test aware member of type : " + member.getClass().getSimpleName() + ", passing testObject for initialization");
                    if(member instanceof BeforeEachMethodInitializable){
                        beforeEachMethodInitializables.add(((BeforeEachMethodInitializable) member));
                    }else {
                        ((TestInitializable) member).init();
                    }
                }
                if(member instanceof TestAware){
                    log.debug("found controller test aware member, of type : " + member.getClass().getSimpleName() +", passing testObject for initialization");
                    ((TestAware) member).setTest(this);
                }
            }
        }
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

}
