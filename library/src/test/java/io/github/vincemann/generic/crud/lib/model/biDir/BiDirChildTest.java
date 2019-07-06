package io.github.vincemann.generic.crud.lib.model.biDir;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

class BiDirChildTest {


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
    private class SecondEntityParent extends IdentifiableEntityImpl<Long> implements BiDirParent{
        @BiDirChildEntity(EntityChild.class)
        private EntityChild entityChild;
    }
    @Getter
    @Setter
    private class EntityParent extends IdentifiableEntityImpl<Long> implements BiDirParent {
        @BiDirChildEntity(EntityChild.class)
        private EntityChild entityChild;
    }

    private EntityChild testEntityChild;
    private EntityParent testEntityParent;
    private SecondEntityParent testSecondEntityParent;

    @BeforeEach
    void setUp() {
        this.testEntityChild= new EntityChild();
        this.testEntityParent = new EntityParent();
        this.testSecondEntityParent = new SecondEntityParent();
    }

    @Test
    void findAndSetParent() throws IllegalAccessException {
        //when
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertNull(testEntityChild.getUnusedParent());
        Assertions.assertNull(testEntityChild.getSecondEntityParent());
        //do
        testEntityChild.findAndSetParent(testEntityParent);
        //then
        Assertions.assertSame(testEntityChild.getEntityParent(),testEntityParent);
        Assertions.assertNull(testEntityChild.getUnusedParent());
        Assertions.assertNull(testEntityChild.getSecondEntityParent());

    }

    @Test
    void addChildToParents() throws IllegalAccessException {
        //when
        testEntityChild.setEntityParent(testEntityParent);
        testEntityChild.setSecondEntityParent(testSecondEntityParent);
        Assertions.assertNull(testEntityParent.getEntityChild());
        Assertions.assertNull(testSecondEntityParent.getEntityChild());
        //do
        testEntityChild.addChildToParents();
        //then
        Assertions.assertSame(testEntityChild,testEntityParent.getEntityChild());
        Assertions.assertSame(testEntityChild,testSecondEntityParent.getEntityChild());
    }

    @Test
    void findAndSetParentIfNull() throws IllegalAccessException {
        //when
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertNull(testEntityChild.getUnusedParent());
        Assertions.assertNull(testEntityChild.getSecondEntityParent());
        //do
        testEntityChild.findAndSetParentIfNull(testEntityParent);
        //then
        Assertions.assertSame(testEntityParent,testEntityChild.getEntityParent());
        Assertions.assertNull(testEntityChild.getUnusedParent());
        Assertions.assertNull(testEntityChild.getSecondEntityParent());
    }

    @Test
    void findAndSetParentIfNotNull() throws IllegalAccessException {
        //when
        EntityParent newEntityParent = new EntityParent();
        testEntityChild.setEntityParent(newEntityParent);
        Assertions.assertNull(testEntityChild.getUnusedParent());
        Assertions.assertNull(testEntityChild.getSecondEntityParent());
        //do
        testEntityChild.findAndSetParentIfNull(testEntityParent);
        //then
        //test entity parent is NOT set
        Assertions.assertSame(newEntityParent,testEntityChild.getEntityParent());
        Assertions.assertNull(testEntityChild.getUnusedParent());
        Assertions.assertNull(testEntityChild.getSecondEntityParent());
    }

    @Test
    void findParentFields() throws IllegalAccessException {
        //when
        testEntityChild.setEntityParent(testEntityParent);
        testEntityChild.setSecondEntityParent(testSecondEntityParent);
        //do
        Field[] parentFields = testEntityChild.findParentFields();
        //then
        Assertions.assertEquals(2,parentFields.length);
        parentFields[0].setAccessible(true);
        parentFields[1].setAccessible(true);
        EntityParent fieldParent = (EntityParent) parentFields[0].get(testEntityChild);
        Assertions.assertSame(testEntityParent,fieldParent);
        SecondEntityParent secondFieldParent = (SecondEntityParent) parentFields[1].get(testEntityChild);
        Assertions.assertSame(testSecondEntityParent,secondFieldParent);
    }

    @Test
    void findParentFieldsWhenParentNull() throws IllegalAccessException {
        //when
        Assertions.assertNull(testEntityChild.getEntityParent());
        Assertions.assertNull(testEntityChild.getSecondEntityParent());
        //do
        Field[] parentFields = testEntityChild.findParentFields();
        //then
        Assertions.assertEquals(2,parentFields.length);
        parentFields[0].setAccessible(true);
        parentFields[1].setAccessible(true);
        EntityParent fieldParent = (EntityParent) parentFields[0].get(testEntityChild);
        Assertions.assertNull(fieldParent);
        EntityParent secondFieldParent = (EntityParent) parentFields[1].get(testEntityChild);
        Assertions.assertNull(secondFieldParent);
    }

    @Test
    void findParents() throws IllegalAccessException {
        //when
        testEntityChild.setEntityParent(testEntityParent);
        testEntityChild.setSecondEntityParent(testSecondEntityParent);
        //do
        Collection<BiDirParent> parents = testEntityChild.findParents();
        //then
        Assertions.assertEquals(2,parents.size());
    }
    @Test
    void findParentsWithOneNullParent() throws IllegalAccessException {
        //when
        testEntityChild.setEntityParent(testEntityParent);
        Assertions.assertNull(testEntityChild.getSecondEntityParent());
        Assertions.assertNull(testEntityChild.getUnusedParent());
        //do
        Collection<BiDirParent> parents = testEntityChild.findParents();
        //then
        Assertions.assertEquals(1,parents.size());
        Optional<BiDirParent> biDirParent = parents.stream().findFirst();
        Assertions.assertTrue(biDirParent.isPresent());
        Assertions.assertSame(testEntityParent,biDirParent.get());
    }

    @Test
    void dismissParents() {
    }

    @Test
    void dismissParent() {
    }

    @AfterEach
    void tearDown() {
        BiDirChild.biDirParentFieldsCache.clear();
        BiDirParent.biDirChildEntityFieldsCache.clear();
        BiDirParent.biDirChildrenCollectionFieldsCache.clear();
    }
}