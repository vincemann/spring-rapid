package com.github.vincemann.springrapid.autobidir.entity.bidir.parent;

import com.github.vincemann.springrapid.autobidir.entity.RelationalEntityManagerUtilImpl;
import com.github.vincemann.springrapid.autobidir.entity.RelationalEntityManagerUtil;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.core.model.IdAwareEntityImpl;

import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentEntity;

import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class RapidRelationalEntityManagerUtilParentTest {

    private class EntityChild extends IdAwareEntityImpl<Long> {
        @BiDirParentEntity
        private EntityParent entityParent;
        private String name;
        private EntityParent unusedParent;
        @BiDirParentEntity
        private SecondEntityParent secondEntityParent;

        public EntityParent getEntityParent() {
            return entityParent;
        }

        public void setEntityParent(EntityParent entityParent) {
            this.entityParent = entityParent;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public EntityParent getUnusedParent() {
            return unusedParent;
        }

        public void setUnusedParent(EntityParent unusedParent) {
            this.unusedParent = unusedParent;
        }

        public SecondEntityParent getSecondEntityParent() {
            return secondEntityParent;
        }

        public void setSecondEntityParent(SecondEntityParent secondEntityParent) {
            this.secondEntityParent = secondEntityParent;
        }
    }


    private class SecondEntityChild extends IdAwareEntityImpl<Long> {
        @BiDirParentEntity
        private EntityParent entityParent;

        public EntityParent getEntityParent() {
            return entityParent;
        }

        public void setEntityParent(EntityParent entityParent) {
            this.entityParent = entityParent;
        }
    }

    private class SecondEntityParent extends IdAwareEntityImpl<Long> {
        @BiDirChildEntity
        private EntityChild entityChild;

        public EntityChild getEntityChild() {
            return entityChild;
        }

        public void setEntityChild(EntityChild entityChild) {
            this.entityChild = entityChild;
        }
    }

    private class EntityParent extends IdAwareEntityImpl<Long> {
        @BiDirChildEntity
        private EntityChild entityChild;
        @BiDirChildCollection(SecondEntityChild.class)
        private Set<SecondEntityChild> secondEntityChildSet = new HashSet<>();

        public EntityChild getEntityChild() {
            return entityChild;
        }

        public void setEntityChild(EntityChild entityChild) {
            this.entityChild = entityChild;
        }

        public Set<SecondEntityChild> getSecondEntityChildSet() {
            return secondEntityChildSet;
        }

        public void setSecondEntityChildSet(Set<SecondEntityChild> secondEntityChildSet) {
            this.secondEntityChildSet = secondEntityChildSet;
        }
    }

    private EntityChild testEntityChild;
    private EntityParent testEntityParent;
    private SecondEntityParent testSecondEntityParent;
    private SecondEntityChild testSecondEntityChild;

    private RelationalEntityManagerUtil relationalEntityManagerUtil;

    @BeforeEach
    void setUp() {
        this.relationalEntityManagerUtil = new RelationalEntityManagerUtilImpl();
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
        Map<Class<IdAwareEntity>,Collection<IdAwareEntity>> childrenCollections = relationalEntityManagerUtil.findBiDirChildCollections(testEntityParent);
        //then
        Assertions.assertEquals(1,childrenCollections.size());
        Map.Entry<Class<IdAwareEntity>,Collection<IdAwareEntity>> entry = childrenCollections.entrySet().stream().findFirst().get();
        Assertions.assertEquals(SecondEntityChild.class,entry.getKey());
        Assertions.assertSame(secondEntityChildSet,entry.getValue());
    }

    @Test
    void getNullChildrenCollection_shouldCreateEmptyCollection()  {
        //given
        testEntityParent.setSecondEntityChildSet(null);
        Map<Class<IdAwareEntity>,Collection<IdAwareEntity>> childrenCollections = relationalEntityManagerUtil.findBiDirChildCollections(testEntityParent);
        for (Map.Entry<Class<IdAwareEntity>,Collection<IdAwareEntity>> collectionClassEntry : childrenCollections.entrySet()) {
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
        Set<? extends IdAwareEntity> children = relationalEntityManagerUtil.findSingleBiDirChildren(testEntityParent);
        //then
        Assertions.assertEquals(1,children.size());
        Assertions.assertSame(testEntityChild,children.stream().findFirst().get());
    }

    @Test
    void getNullChildren()  {
        //given
        testEntityParent.setEntityChild(null);
        //when
        Set<? extends IdAwareEntity> children = relationalEntityManagerUtil.findSingleBiDirChildren(testEntityParent);
        //then
        Assertions.assertTrue(children.isEmpty());
    }
}