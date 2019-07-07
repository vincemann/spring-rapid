package io.github.vincemann.generic.crud.lib.model.biDir;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownChildTypeException;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Field;
import java.util.*;

class BiDirParentTest {

    @Getter
    @Setter
    private class EntityChild extends IdentifiableEntityImpl<Long> implements BiDirChild {
        @BiDirParentEntity
        private EntityParent entityParent;
        private String name;
        private EntityParent unusedParent;
        @BiDirParentEntity
        private SecondEntityParent secondEntityParent;
    }

    @Getter
    @Setter
    private class SecondEntityChild extends IdentifiableEntityImpl<Long> implements BiDirChild {
        @BiDirParentEntity
        private EntityParent entityParent;
    }
    @Getter
    @Setter
    private class SecondEntityParent extends IdentifiableEntityImpl<Long> implements BiDirParent{
        @BiDirChildEntity(EntityChild.class)
        private EntityChild entityChild;
    }
    @Getter
    @Setter
    private class EntityParent extends IdentifiableEntityImpl<Long> implements BiDirParent {
        @BiDirChildEntity(EntityChild.class)
        private EntityChild entityChild;
        @BiDirChildCollection(SecondEntityChild.class)
        private Set<SecondEntityChild> secondEntityChildSet = new HashSet<>();
    }

    private EntityChild testEntityChild;
    private EntityParent testEntityParent;
    private SecondEntityParent testSecondEntityParent;
    private SecondEntityChild testSecondEntityChild;

    @BeforeEach
    void setUp() {
        this.testEntityChild= new EntityChild();
        testEntityChild.setId(1L);
        this.testEntityParent = new EntityParent();
        testEntityParent.setId(2L);
        this.testSecondEntityParent = new SecondEntityParent();
        testSecondEntityParent.setId(3L);
        this.testSecondEntityChild = new SecondEntityChild();
        testSecondEntityChild.setId(4L);
    }


    @Test
    void dismissChildrensParent() throws IllegalAccessException {
        //when
        testEntityParent.setEntityChild(testEntityChild);
        testEntityChild.setEntityParent(testEntityParent);
        //do
        testEntityParent.dismissChildrensParent();
        //then
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertNotNull(testEntityParent.getEntityChild());
    }
    @Test
    void dismissChildrensCollectionParent() throws IllegalAccessException {
        //when
        testEntityParent.setSecondEntityChildSet(new HashSet<>(Arrays.asList(testSecondEntityChild)));
        testSecondEntityChild.setEntityParent(testEntityParent);
        //do
        testEntityParent.dismissChildrensParent();
        //then
        Assertions.assertFalse(testEntityParent.getSecondEntityChildSet().stream().findFirst().isPresent());
        Assertions.assertNull(testSecondEntityChild.getEntityParent());
    }
    @Test
    void dismissAllChildrensParent() throws IllegalAccessException {
        //when
        testEntityParent.setSecondEntityChildSet(new HashSet<>(Arrays.asList(testSecondEntityChild)));
        testSecondEntityChild.setEntityParent(testEntityParent);
        testEntityParent.setEntityChild(testEntityChild);
        testEntityChild.setEntityParent(testEntityParent);
        //do
        testEntityParent.dismissChildrensParent();
        //then
        Assertions.assertFalse(testEntityParent.getSecondEntityChildSet().stream().findFirst().isPresent());
        Assertions.assertNull(testSecondEntityChild.getEntityParent());
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertNotNull(testEntityParent.getEntityChild());
    }

    @Test
    void addChild() throws IllegalAccessException {
        //do
        testEntityParent.addChild(testEntityChild);
        //then
        Assertions.assertSame(testEntityChild,testEntityParent.getEntityChild());
    }

    @Test
    void addChildToCollection() throws IllegalAccessException {
        //when
        Assertions.assertNotNull(testEntityParent.getSecondEntityChildSet());
        //do
        testEntityParent.addChild(testSecondEntityChild);
        //then
        Assertions.assertEquals(1,testEntityParent.getSecondEntityChildSet().size());
        Assertions.assertSame(testSecondEntityChild,testEntityParent.getSecondEntityChildSet().stream().findFirst().get());
    }

    @Test
    void addChildToNullCollection() {
        //when
        testEntityParent.setSecondEntityChildSet(null);
        //do
        Assertions.assertThrows(UnknownChildTypeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                testEntityParent.addChild(testSecondEntityChild);
            }
        });
    }

    @Test
    void dismissChild() throws IllegalAccessException {
        //when
        testEntityParent.setEntityChild(testEntityChild);
        //do
        testEntityParent.dismissChild(testEntityChild);
        //then
        Assertions.assertNull(testEntityParent.getEntityChild());
    }

    @Test
    void dismissChildFromCollection() throws IllegalAccessException {
        //when
        testEntityParent.setSecondEntityChildSet(new HashSet<>(Collections.singleton(testSecondEntityChild)));
        //do
        testEntityParent.dismissChild(testSecondEntityChild);
        //then
        Assertions.assertTrue(testEntityParent.getSecondEntityChildSet().isEmpty());
    }

    @Test
    void dismissChildFromFilledCollection() throws IllegalAccessException {
        //when
        SecondEntityChild second = new SecondEntityChild();
        second.setId(99L);
        Set<SecondEntityChild> secondEntityChildren = new HashSet<>();
        secondEntityChildren.add(testSecondEntityChild);
        secondEntityChildren.add(second);
        testEntityParent.setSecondEntityChildSet(secondEntityChildren);
        //do
        testEntityParent.dismissChild(testSecondEntityChild);
        //then
        Assertions.assertEquals(1,testEntityParent.getSecondEntityChildSet().size());
        Assertions.assertSame(second,testEntityParent.getSecondEntityChildSet().stream().findFirst().get());
    }

    @Test
    void findChildrenCollectionFields() {
        //do
        Field[] childrenCollectionFields = testEntityParent.findChildrenCollectionFields();
        //then
        Assertions.assertEquals(1,childrenCollectionFields.length);
        Assertions.assertEquals("secondEntityChildSet",childrenCollectionFields[0].getName());
    }

    @Test
    void findChildrenEntityFields() {
        //do
        Field[] childrenEntityFields = testEntityParent.findChildrenEntityFields();
        Assertions.assertEquals(1,childrenEntityFields.length);
        Assertions.assertEquals("entityChild",childrenEntityFields[0].getName());

    }

    @Test
    void getChildrenCollections() throws IllegalAccessException {
        //when
        HashSet<SecondEntityChild> secondEntityChildSet = new HashSet<>();
        testEntityParent.setSecondEntityChildSet(secondEntityChildSet);
        //do
        Map<Collection<? extends BiDirChild>, Class<? extends BiDirChild>> childrenCollections = testEntityParent.getChildrenCollections();
        //then
        Assertions.assertEquals(1,childrenCollections.size());
        Map.Entry<Collection<? extends BiDirChild>, Class<? extends BiDirChild>> entry = childrenCollections.entrySet().stream().findFirst().get();
        Assertions.assertEquals(SecondEntityChild.class,entry.getValue());
        Assertions.assertSame(secondEntityChildSet,entry.getKey());
    }

    @Test
    void getNullChildrenCollection() throws IllegalAccessException {
        //when
        testEntityParent.setSecondEntityChildSet(null);
        //do
        Map<Collection<? extends BiDirChild>, Class<? extends BiDirChild>> childrenCollections = testEntityParent.getChildrenCollections();
        //then
        Assertions.assertTrue(childrenCollections.isEmpty());
    }

    @Test
    void getChildren() throws IllegalAccessException {
        //when
        testEntityParent.setEntityChild(testEntityChild);
        //do
        Set<? extends BiDirChild> children = testEntityParent.getChildren();
        //then
        Assertions.assertEquals(1,children.size());
        Assertions.assertSame(testEntityChild,children.stream().findFirst().get());
    }

    @Test
    void getNullChildren() throws IllegalAccessException {
        //when
        testEntityParent.setEntityChild(null);
        //do
        Set<? extends BiDirChild> children = testEntityParent.getChildren();
        //then
        Assertions.assertTrue(children.isEmpty());
    }
}