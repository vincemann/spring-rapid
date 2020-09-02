package com.github.vincemann.springrapid.entityrelationship.model.biDir.parent;

import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildEntity;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    private class SecondEntityParent extends IdentifiableEntityImpl<Long> implements BiDirParent {
        @BiDirChildEntity
        private EntityChild entityChild;
    }
    @Getter
    @Setter
    private class EntityParent extends IdentifiableEntityImpl<Long> implements BiDirParent {
        @BiDirChildEntity
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
    void dismissChildrensParent()  {
        //given
        testEntityParent.setEntityChild(testEntityChild);
        testEntityChild.setEntityParent(testEntityParent);
        //when
        testEntityParent.dismissChildrensParent();
        //then
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertNotNull(testEntityParent.getEntityChild());
    }
    @Test
    void dismissChildrensCollectionParent()  {
        //given
        testEntityParent.setSecondEntityChildSet(new HashSet<>(Lists.newArrayList(testSecondEntityChild)));
        testSecondEntityChild.setEntityParent(testEntityParent);
        //when
        testEntityParent.dismissChildrensParent();
        //then
        Assertions.assertFalse(testEntityParent.getSecondEntityChildSet().stream().findFirst().isPresent());
        Assertions.assertNull(testSecondEntityChild.getEntityParent());
    }
    @Test
    void dismissAllChildrensParent()  {
        //given
        testEntityParent.setSecondEntityChildSet(new HashSet<>(Lists.newArrayList(testSecondEntityChild)));
        testSecondEntityChild.setEntityParent(testEntityParent);
        testEntityParent.setEntityChild(testEntityChild);
        testEntityChild.setEntityParent(testEntityParent);
        //when
        testEntityParent.dismissChildrensParent();
        //then
        Assertions.assertFalse(testEntityParent.getSecondEntityChildSet().stream().findFirst().isPresent());
        Assertions.assertNull(testSecondEntityChild.getEntityParent());
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertNotNull(testEntityParent.getEntityChild());
    }

    @Test
    void addChild()  {
        //when
        testEntityParent.addBiDirChild(testEntityChild);
        //then
        Assertions.assertSame(testEntityChild,testEntityParent.getEntityChild());
    }

    @Test
    void addChildToCollection()  {
        //given
        Assertions.assertNotNull(testEntityParent.getSecondEntityChildSet());
        //when
        testEntityParent.addBiDirChild(testSecondEntityChild);
        //then
        Assertions.assertEquals(1,testEntityParent.getSecondEntityChildSet().size());
        Assertions.assertSame(testSecondEntityChild,testEntityParent.getSecondEntityChildSet().stream().findFirst().get());
    }

    @Test
    void addChildToNullCollection_shouldAutoCreateCollectionAndWork()  {
        //given
        testEntityParent.setSecondEntityChildSet(null);
        testEntityParent.addBiDirChild(testSecondEntityChild);
        Assertions.assertEquals(testSecondEntityChild,testEntityParent.getSecondEntityChildSet().stream().findFirst().get());
    }

    @Test
    void dismissChild()  {
        //given
        testEntityParent.setEntityChild(testEntityChild);
        //when
        testEntityParent.dismissBiDirChild(testEntityChild);
        //then
        Assertions.assertNull(testEntityParent.getEntityChild());
    }

    @Test
    void dismissChildFromCollection()  {
        //given
        testEntityParent.setSecondEntityChildSet(new HashSet<>(Collections.singleton(testSecondEntityChild)));
        //when
        testEntityParent.dismissBiDirChild(testSecondEntityChild);
        //then
        Assertions.assertTrue(testEntityParent.getSecondEntityChildSet().isEmpty());
    }

    @Test
    void dismissChildFromFilledCollection()  {
        //given
        SecondEntityChild second = new SecondEntityChild();
        second.setId(99L);
        Set<SecondEntityChild> secondEntityChildren = new HashSet<>();
        secondEntityChildren.add(testSecondEntityChild);
        secondEntityChildren.add(second);
        testEntityParent.setSecondEntityChildSet(secondEntityChildren);
        //when
        testEntityParent.dismissBiDirChild(testSecondEntityChild);
        //then
        Assertions.assertEquals(1,testEntityParent.getSecondEntityChildSet().size());
        Assertions.assertSame(second,testEntityParent.getSecondEntityChildSet().stream().findFirst().get());
    }

//    @Test
//    void findChildrenCollectionFields() {
//        //when
//        Field[] childrenCollectionFields = testEntityParent.findChildrenCollectionFields();
//        //then
//        Assertions.assertEquals(1,childrenCollectionFields.length);
//        Assertions.assertEquals("secondEntityChildSet",childrenCollectionFields[0].getName());
//    }
//
//    @Test
//    void findChildrenEntityFields() {
//        //when
//        Field[] childrenEntityFields = testEntityParent.findChildrenEntityFields();
//        //then
//        Assertions.assertEquals(1,childrenEntityFields.length);
//        Assertions.assertEquals("entityChild",childrenEntityFields[0].getName());
//
//    }

    @Test
    void getChildrenCollections()  {
        //given
        HashSet<SecondEntityChild> secondEntityChildSet = new HashSet<>();
        testEntityParent.setSecondEntityChildSet(secondEntityChildSet);
        //when
        Map<Collection<BiDirChild>, Class<BiDirChild>> childrenCollections = testEntityParent.findAllBiDirChildCollections();
        //then
        Assertions.assertEquals(1,childrenCollections.size());
        Map.Entry<Collection<BiDirChild>, Class<BiDirChild>> entry = childrenCollections.entrySet().stream().findFirst().get();
        Assertions.assertEquals(SecondEntityChild.class,entry.getValue());
        Assertions.assertSame(secondEntityChildSet,entry.getKey());
    }

    @Test
    void getNullChildrenCollection_shouldCreateEmptyCollection()  {
        //given
        testEntityParent.setSecondEntityChildSet(null);
        Map<Collection<BiDirChild>, Class<BiDirChild>> childrenCollections = testEntityParent.findAllBiDirChildCollections();
        for (Map.Entry<Collection<BiDirChild>, Class<BiDirChild>> collectionClassEntry : childrenCollections.entrySet()) {
            Assertions.assertNotNull(collectionClassEntry.getKey());
            Assertions.assertTrue(collectionClassEntry.getKey().isEmpty());
            Assertions.assertTrue(collectionClassEntry.getKey() instanceof Set);
        }
    }

    @Test
    void getChildren()  {
        //given
        testEntityParent.setEntityChild(testEntityChild);
        //when
        Set<? extends BiDirChild> children = testEntityParent.findBiDirSingleChildren();
        //then
        Assertions.assertEquals(1,children.size());
        Assertions.assertSame(testEntityChild,children.stream().findFirst().get());
    }

    @Test
    void getNullChildren()  {
        //given
        testEntityParent.setEntityChild(null);
        //when
        Set<? extends BiDirChild> children = testEntityParent.findBiDirSingleChildren();
        //then
        Assertions.assertTrue(children.isEmpty());
    }
}