package com.github.vincemann.springrapid.autobidir.entity.bidir.child;

import com.github.vincemann.springrapid.autobidir.entity.RelationalEntityManagerUtil;
import com.github.vincemann.springrapid.autobidir.entity.RelationalEntityManagerUtilImpl;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildEntity;
import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import org.hibernate.UnknownEntityTypeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.util.ReflectionUtils;

import java.util.Collection;
import java.util.Optional;

class RapidRelationalEntityManagerUtilChildTest {


    private class EntityChild extends IdentifiableEntityImpl<Long>  {
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

    private class SecondEntityParent extends IdentifiableEntityImpl<Long> {
        @BiDirChildEntity
        private EntityChild entityChild;

        public EntityChild getEntityChild() {
            return entityChild;
        }

        public void setEntityChild(EntityChild entityChild) {
            this.entityChild = entityChild;
        }
    }

    private class EntityParent extends IdentifiableEntityImpl<Long> {
        @BiDirChildEntity
        private EntityChild entityChild;

        public EntityChild getEntityChild() {
            return entityChild;
        }

        public void setEntityChild(EntityChild entityChild) {
            this.entityChild = entityChild;
        }
    }

    private EntityChild testEntityChild;
    private EntityParent testEntityParent;
    private SecondEntityParent testSecondEntityParent;

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
    }

    @Test
    void findAndLinkParent()  {
        //given
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertNull(testEntityChild.getUnusedParent());
        Assertions.assertNull(testEntityChild.getSecondEntityParent());
        //when
        relationalEntityManagerUtil.linkBiDirParent(testEntityChild,testEntityParent);
        //then
        Assertions.assertSame(testEntityChild.getEntityParent(),testEntityParent);
        Assertions.assertNull(testEntityChild.getUnusedParent());
        Assertions.assertNull(testEntityChild.getSecondEntityParent());

    }

//    @Test
//    void addChildToParents()  {
//        //given
//        testEntityChild.setEntityParent(testEntityParent);
//        testEntityChild.setSecondEntityParent(testSecondEntityParent);
//        Assertions.assertNull(testEntityParent.getEntityChild());
//        Assertions.assertNull(testSecondEntityParent.getEntityChild());
//        //when
//        testEntityChild.linkToBiDirParents();
//        //then
//        Assertions.assertSame(testEntityChild,testEntityParent.getEntityChild());
//        Assertions.assertSame(testEntityChild,testSecondEntityParent.getEntityChild());
//    }

//    @Test
//    void findAndSetParentIfNull()  {
//        //given
//        Assertions.assertNull(testEntityChild.getEntityParent());
//        Assertions.assertNull(testEntityChild.getUnusedParent());
//        Assertions.assertNull(testEntityChild.getSecondEntityParent());
//        //when
//        testEntityChild.linkBiDirParentIfNonePresent(testEntityParent);
//        //then
//        Assertions.assertSame(testEntityParent,testEntityChild.getEntityParent());
//        Assertions.assertNull(testEntityChild.getUnusedParent());
//        Assertions.assertNull(testEntityChild.getSecondEntityParent());
//    }

//    @Test
//    void findAndSetParentIfNotNull()  {
//        //given
//        EntityParent newEntityParent = new EntityParent();
//        newEntityParent.setId(99L);
//        testEntityChild.setEntityParent(newEntityParent);
//        Assertions.assertNull(testEntityChild.getUnusedParent());
//        Assertions.assertNull(testEntityChild.getSecondEntityParent());
//        //when
//        testEntityChild.linkBiDirParentIfNonePresent(testEntityParent);
//        //then
//        //test entity parent is NOT set
//        Assertions.assertSame(newEntityParent,testEntityChild.getEntityParent());
//        Assertions.assertNull(testEntityChild.getUnusedParent());
//        Assertions.assertNull(testEntityChild.getSecondEntityParent());
//    }

//    @Test
//    void findParentFields()  {
//        //given
//        testEntityChild.setEntityParent(testEntityParent);
//        testEntityChild.setSecondEntityParent(testSecondEntityParent);
//        //when
//        Field[] parentFields = testEntityChild.findBiDirParents();
//        //then
//        Assertions.assertEquals(2,parentFields.length);
//        parentFields[0].setAccessible(true);
//        parentFields[1].setAccessible(true);
//        EntityParent fieldParent = (EntityParent) parentFields[0].get(testEntityChild);
//        Assertions.assertSame(testEntityParent,fieldParent);
//        SecondEntityParent secondFieldParent = (SecondEntityParent) parentFields[1].get(testEntityChild);
//        Assertions.assertSame(testSecondEntityParent,secondFieldParent);
//    }
//
//    @Test
//    void findParentFieldsWhenParentNull()  {
//        //given
//        Assertions.assertNull(testEntityChild.getEntityParent());
//        Assertions.assertNull(testEntityChild.getSecondEntityParent());
//        //when
//        Field[] parentFields = testEntityChild.findParentFields();
//        //then
//        Assertions.assertEquals(2,parentFields.length);
//        parentFields[0].setAccessible(true);
//        parentFields[1].setAccessible(true);
//        EntityParent fieldParent = (EntityParent) parentFields[0].get(testEntityChild);
//        Assertions.assertNull(fieldParent);
//        EntityParent secondFieldParent = (EntityParent) parentFields[1].get(testEntityChild);
//        Assertions.assertNull(secondFieldParent);
//    }

    @Test
    void findParents()  {
        //given
        testEntityChild.setEntityParent(testEntityParent);
        testEntityChild.setSecondEntityParent(testSecondEntityParent);
        //when
        Collection<IdentifiableEntity> parents = relationalEntityManagerUtil.findSingleBiDirParents(testEntityChild);
        //then
        Assertions.assertEquals(2,parents.size());
    }
    @Test
    void findParentsWithOneNullParent()  {
        //given
        testEntityChild.setEntityParent(testEntityParent);
        Assertions.assertNull(testEntityChild.getSecondEntityParent());
        Assertions.assertNull(testEntityChild.getUnusedParent());
        //when
        Collection<IdentifiableEntity> parents = relationalEntityManagerUtil.findSingleBiDirParents(testEntityChild);
        //then
        Assertions.assertEquals(1,parents.size());
        Optional<IdentifiableEntity> biDirParent = parents.stream().findFirst();
        Assertions.assertTrue(biDirParent.isPresent());
        Assertions.assertSame(testEntityParent,biDirParent.get());
    }

    @Test
    void unlinkParents()  {
        //given
        testEntityChild.setEntityParent(testEntityParent);
        testEntityChild.setSecondEntityParent(testSecondEntityParent);
        //when
        relationalEntityManagerUtil.unlinkBiDirParentsFrom(testEntityChild);
        //then
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertNull(testEntityChild.getSecondEntityParent());
    }

    @Test
    void unlinkParent()  {
        //given
        testEntityChild.setEntityParent(testEntityParent);
        //when
        relationalEntityManagerUtil.unlinkBiDirParent(testEntityChild,testEntityParent);
        //then
        Assertions.assertNull(testEntityChild.getEntityParent());
    }

    @Test
    void unlinkUnknownParent() {
        //given
        testEntityChild.setSecondEntityParent(testSecondEntityParent);
        //when
        Assertions.assertThrows(UnknownEntityTypeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                relationalEntityManagerUtil.unlinkBiDirParent(testEntityChild,testEntityParent);
            }
        });
    }

    @Test
    void unlinkParentWhenMultiplePresent()  {
        //given
        testEntityChild.setEntityParent(testEntityParent);
        testEntityChild.setSecondEntityParent(testSecondEntityParent);
        //when
        relationalEntityManagerUtil.unlinkBiDirParent(testEntityChild,testEntityParent);
        //then
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertSame(testSecondEntityParent,testEntityChild.getSecondEntityParent());

    }

    @AfterEach
    void tearDown() {
        ReflectionUtils.clearCache();
//        BiDirChild.biDirParentFieldsCache.clear();
//        BiDirParent.biDirChildEntityFieldsCache.clear();
//        BiDirParent.biDirChildrenCollectionFieldsCache.clear();
    }
}