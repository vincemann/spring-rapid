package com.github.vincemann.springrapid.autobidir.model.biDir.parent;

import com.github.vincemann.springrapid.autobidir.RapidRelationalEntityManagerUtil;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManagerUtil;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;

import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildEntity;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class RapidRelationalEntityManagerUtilParentTest {

    @Getter
    @Setter
    private class EntityChild extends IdentifiableEntityImpl<Long>  {
        @BiDirParentEntity
        private EntityParent entityParent;
        private String name;
        private EntityParent unusedParent;
        @BiDirParentEntity
        private SecondEntityParent secondEntityParent;
    }

    @Getter
    @Setter
    private class SecondEntityChild extends IdentifiableEntityImpl<Long>  {
        @BiDirParentEntity
        private EntityParent entityParent;
    }
    @Getter
    @Setter
    private class SecondEntityParent extends IdentifiableEntityImpl<Long> {
        @BiDirChildEntity
        private EntityChild entityChild;
    }
    @Getter
    @Setter
    private class EntityParent extends IdentifiableEntityImpl<Long> {
        @BiDirChildEntity
        private EntityChild entityChild;
        @BiDirChildCollection(SecondEntityChild.class)
        private Set<SecondEntityChild> secondEntityChildSet = new HashSet<>();
    }

    private EntityChild testEntityChild;
    private EntityParent testEntityParent;
    private SecondEntityParent testSecondEntityParent;
    private SecondEntityChild testSecondEntityChild;

    private RelationalEntityManagerUtil relationalEntityManagerUtil;

    @BeforeEach
    void setUp() {
        this.relationalEntityManagerUtil = new RapidRelationalEntityManagerUtil();
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
    void unlinkChildrensParent()  {
        //given
        testEntityParent.setEntityChild(testEntityChild);
        testEntityChild.setEntityParent(testEntityParent);
        //when

        relationalEntityManagerUtil.unlinkBiDirChildrensParent(testEntityParent);
        //then
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertNotNull(testEntityParent.getEntityChild());
    }
    @Test
    void unlinkChildrensCollectionParent()  {
        //given
        testEntityParent.setSecondEntityChildSet(new HashSet<>(Lists.newArrayList(testSecondEntityChild)));
        testSecondEntityChild.setEntityParent(testEntityParent);
        //when
        relationalEntityManagerUtil.unlinkBiDirChildrensParent(testEntityParent);
        //then
        Assertions.assertTrue(testEntityParent.getSecondEntityChildSet().stream().findFirst().isPresent());
        Assertions.assertNull(testSecondEntityChild.getEntityParent());
    }
    @Test
    void unlinkAllChildrensParent()  {
        //given
        testEntityParent.setSecondEntityChildSet(new HashSet<>(Lists.newArrayList(testSecondEntityChild)));
        testSecondEntityChild.setEntityParent(testEntityParent);
        testEntityParent.setEntityChild(testEntityChild);
        testEntityChild.setEntityParent(testEntityParent);
        //when
        relationalEntityManagerUtil.unlinkBiDirChildrensParent(testEntityParent);
        //then
        Assertions.assertTrue(testEntityParent.getSecondEntityChildSet().stream().findFirst().isPresent());
        Assertions.assertNull(testSecondEntityChild.getEntityParent());
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertNotNull(testEntityParent.getEntityChild());
    }

    @Test
    void addChild()  {
        //when
        relationalEntityManagerUtil.linkBiDirChild(testEntityParent, testEntityChild);
        //then
        Assertions.assertSame(testEntityChild,testEntityParent.getEntityChild());
    }

    @Test
    void addChildToCollection()  {
        //given
        Assertions.assertNotNull(testEntityParent.getSecondEntityChildSet());
        //when
        relationalEntityManagerUtil.linkBiDirChild(testEntityParent,testSecondEntityChild);
        //then
        Assertions.assertEquals(1,testEntityParent.getSecondEntityChildSet().size());
        Assertions.assertSame(testSecondEntityChild,testEntityParent.getSecondEntityChildSet().stream().findFirst().get());
    }

    // not supported
//    @Test
//    void addChildToNullCollection_shouldAutoCreateCollectionAndWork()  {
//        //given
//        testEntityParent.setSecondEntityChildSet(null);
//        relationalEntityManagerUtil.linkBiDirChild(testEntityParent,testSecondEntityChild);
//        Assertions.assertEquals(testSecondEntityChild,testEntityParent.getSecondEntityChildSet().stream().findFirst().get());
//    }

    @Test
    void unlinkChild()  {
        //given
        testEntityParent.setEntityChild(testEntityChild);
        //when
        relationalEntityManagerUtil.unlinkBiDirChild(testEntityParent,testEntityChild);
        //then
        Assertions.assertNull(testEntityParent.getEntityChild());
    }

    @Test
    void unlinkChildFromCollection()  {
        //given
        testEntityParent.setSecondEntityChildSet(new HashSet<>(Collections.singleton(testSecondEntityChild)));
        //when
        relationalEntityManagerUtil.unlinkBiDirChild(testEntityParent,testSecondEntityChild);
        //then
        Assertions.assertTrue(testEntityParent.getSecondEntityChildSet().isEmpty());
    }

    @Test
    void unlinkChildFromFilledCollection()  {
        //given
        SecondEntityChild second = new SecondEntityChild();
        second.setId(99L);
        Set<SecondEntityChild> secondEntityChildren = new HashSet<>();
        secondEntityChildren.add(testSecondEntityChild);
        secondEntityChildren.add(second);
        testEntityParent.setSecondEntityChildSet(secondEntityChildren);
        //when
        relationalEntityManagerUtil.unlinkBiDirChild(testEntityParent,testSecondEntityChild);
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
        Map<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> childrenCollections = relationalEntityManagerUtil.findBiDirChildCollections(testEntityParent);
        //then
        Assertions.assertEquals(1,childrenCollections.size());
        Map.Entry<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> entry = childrenCollections.entrySet().stream().findFirst().get();
        Assertions.assertEquals(SecondEntityChild.class,entry.getKey());
        Assertions.assertSame(secondEntityChildSet,entry.getValue());
    }

    @Test
    void getNullChildrenCollection_shouldCreateEmptyCollection()  {
        //given
        testEntityParent.setSecondEntityChildSet(null);
        Map<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> childrenCollections = relationalEntityManagerUtil.findBiDirChildCollections(testEntityParent);
        for (Map.Entry<Class<IdentifiableEntity>,Collection<IdentifiableEntity>> collectionClassEntry : childrenCollections.entrySet()) {
            Assertions.assertNotNull(collectionClassEntry.getValue());
            Assertions.assertTrue(collectionClassEntry.getValue().isEmpty());
            Assertions.assertTrue(collectionClassEntry.getValue() instanceof Set);
        }
    }

    @Test
    void getChildren()  {
        //given
        testEntityParent.setEntityChild(testEntityChild);
        //when
        Set<? extends IdentifiableEntity> children = relationalEntityManagerUtil.findSingleBiDirChildren(testEntityParent);
        //then
        Assertions.assertEquals(1,children.size());
        Assertions.assertSame(testEntityChild,children.stream().findFirst().get());
    }

    @Test
    void getNullChildren()  {
        //given
        testEntityParent.setEntityChild(null);
        //when
        Set<? extends IdentifiableEntity> children = relationalEntityManagerUtil.findSingleBiDirChildren(testEntityParent);
        //then
        Assertions.assertTrue(children.isEmpty());
    }
}