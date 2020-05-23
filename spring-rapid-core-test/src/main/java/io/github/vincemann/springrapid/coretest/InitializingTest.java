package io.github.vincemann.springrapid.coretest;

import io.github.vincemann.springrapid.commons.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;
import java.util.*;

/**
 * BaseClass for Tests that wish to initialize members that implement {@link BeforeEachMethodInitializable}s and/or {@link TestContextAware} and/or
 * {@link TestInitializable}.
 */
@Slf4j
public abstract class InitializingTest{

    private List<TestInitializable> initializables;
    private List<BeforeEachMethodInitializable> beforeEachMethodInitializables;
    private List<TestContextAware> testContextAwareList;


    private boolean init = false;


    @BeforeEach
    public void setup() throws Exception{
        if(!init){
            List<Field> testMemberFields = getAllFields(new ArrayList<>(), this.getClass());
            initializables = findMember(testMemberFields,TestInitializable.class);
            beforeEachMethodInitializables = findMember(testMemberFields,BeforeEachMethodInitializable.class);
            testContextAwareList = findMember(testMemberFields,TestContextAware.class);

            //init all components in spring container
            initializables.forEach(e -> {
                    log.debug("calling init method of  bean : " +e);
                    e.init();
            });
            testContextAwareList.forEach(a -> {
                    log.debug("giving test context to bean : " +a);
                    a.setTestContext(this);
            });
            init=true;
        }
        beforeEachMethodInitializables.forEach(e -> {
                log.debug("calling init method of  bean : " +e);
                e.init();
        });
    }

    private <T> List<T> findMember(List<Field> memberFields, Class<T> type){
        List<T> members = new ArrayList<>();
        for (Field memberField : memberFields) {
            if(type.isAssignableFrom(memberField.getType())){
                try {
                    memberField.setAccessible(true);
                    T member = (T) memberField.get(this);
                    if(member!=null)
                        members.add(member);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
       return members;
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Lists.newArrayList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

}
