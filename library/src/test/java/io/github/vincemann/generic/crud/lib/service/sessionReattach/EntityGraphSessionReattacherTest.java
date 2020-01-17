package io.github.vincemann.generic.crud.lib.service.sessionReattach;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EntityGraphSessionReattacherTest {

    @AllArgsConstructor
    @Entity
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    private abstract class Person extends IdentifiableEntityImpl<Long> {
        int height;
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class Parent extends Person {
        private String name;
        @OneToMany
        private Set<Child> children;

        @Override
        public String toString() {
            return "Parent{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class UniDirParent extends Person {
        private String name;
        private String hey;
        private UniDirChild uniDirChild;
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class UniDirChild extends Person {
        private String name;
        private String heyho;
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class Child extends Person {
        private String name;
        private String fieldWithNullValue;
        @OneToMany
        private Set<GrandChild> grandChildren;
        @ManyToOne
        private Parent parent;
        private NoEntity noEntity;
        private UniDirParent uniDirParent;

        @Override
        public String toString() {
            return "Child{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class GrandChild extends Person {
        private String name;
        private Set<NoEntity> noEntities;
        private Set<NoEntity> nullElementList;
        @OneToMany
        private Set<LastEntity> lastEntities;

        @Override
        public String toString() {
            return "GrandChild{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class LastEntity extends Person {
        private String iAmTheLastNote;
        @ManyToOne
        private GrandChild grandChild;

        @Override
        public String toString() {
            return "LastEntity{" +
                    "iAmTheLastNote='" + iAmTheLastNote + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    private class NoEntity extends IdentifiableEntityImpl<Long>{
        private String shouldNotFind;
        private int pleaseNotFindThis;
    }

    private EntityGraphSessionReattacher sessionReattachmentHelper;
    private SessionReattacher sessionReattacher;

    @BeforeEach
    void setUp() {
        sessionReattacher = mock(SessionReattacher.class);
        this.sessionReattachmentHelper = new EntityGraphSessionReattacher(sessionReattacher);
    }

    @Test
    void attachEntityGraphToSession() {
//GIVEN
        GrandChild grandChild1 = new GrandChild();
        grandChild1.setName("grandchild 1");
        grandChild1.setNoEntities(new HashSet<>(Arrays.asList(new NoEntity("dont",12))));
        grandChild1.setNullElementList(new HashSet<>(Arrays.asList(null,null)));

        GrandChild grandChild2 = new GrandChild();
        grandChild2.setName("grandchild2");

        UniDirChild uniDirChild = new UniDirChild();
        uniDirChild.setHeyho("heyho");
        uniDirChild.setName("uniDirChild");

        UniDirParent uniDirParent = new UniDirParent();
        uniDirParent.setHey("hey");
        uniDirParent.setName("uniDirParent");
        uniDirParent.setUniDirChild(uniDirChild);

        Child child = new Child();
        child.setName("child");
        child.setGrandChildren(new HashSet<>(Arrays.asList(grandChild1,grandChild2)));
        child.setNoEntity(new NoEntity("please dont find",99));
        child.setFieldWithNullValue(null);
        child.setUniDirParent(uniDirParent);

        Parent parent = new Parent();
        parent.setHeight(182);
        parent.setName("parent");
        parent.setChildren(Collections.singleton(child));

        child.setParent(parent);

        LastEntity lastEntity1 = new LastEntity();
        lastEntity1.setIAmTheLastNote("i am 1");
        lastEntity1.setHeight(1);
        lastEntity1.setGrandChild(grandChild1);

        LastEntity lastEntity2 = new LastEntity();
        lastEntity2.setIAmTheLastNote("i am 2");
        lastEntity2.setHeight(2);
        lastEntity2.setGrandChild(grandChild2);

        LastEntity lastEntity3 = new LastEntity();
        lastEntity3.setIAmTheLastNote("i am 3");
        lastEntity3.setHeight(3);
        lastEntity3.setGrandChild(grandChild1);

        grandChild1.setLastEntities(new HashSet<>(Arrays.asList(lastEntity1,lastEntity3)));
        grandChild2.setLastEntities(new HashSet<>(Arrays.asList(lastEntity2)));

        //now setting ids for entities that should be found
        lastEntity2.setId(1L);
        uniDirParent.setId(2L);
        uniDirChild.setId(3L);
        grandChild1.setId(4L);

        List<Object> entitiesReattachedToSession_byHelper = new ArrayList<>();
        when(sessionReattacher.attachToCurrentSession(any())).then((invocation) -> {
            Object argument = (invocation.getArguments()[0]);
            entitiesReattachedToSession_byHelper.add(argument);
            //we dont want to call the actual method, since we are not testing the SessionReattacher
            return true;
        });

//WHEN
        sessionReattachmentHelper.attachEntityGraphToCurrentSession(parent);
//THEN
//        for (Object o : entitiesReattachedToSession_byHelper) {
//            System.out.println(o);
//        }
        
        Assertions.assertEquals(4,entitiesReattachedToSession_byHelper.size());
        Assertions.assertTrue(entitiesReattachedToSession_byHelper.contains(lastEntity2));
        Assertions.assertTrue(entitiesReattachedToSession_byHelper.contains(uniDirChild));
        Assertions.assertTrue(entitiesReattachedToSession_byHelper.contains(uniDirParent));
        Assertions.assertTrue(entitiesReattachedToSession_byHelper.contains(grandChild1));

    }
}