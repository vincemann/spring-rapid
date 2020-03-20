package io.github.vincemann.generic.crud.lib.test.compare;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;

import java.util.Set;

class LevelOneFullComparatorTest {


    LevelOneFullComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new LevelOneFullComparator();
    }

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    class Parent extends IdentifiableEntityImpl<Long> {
        String name;
        int age;
        Child child;
        Set<Child> childSet;

        public Parent(Parent copy){
            this(copy.name,copy.age,copy.child,copy.childSet);
            this.setId(copy.getId());
        }
    }

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    class Child extends IdentifiableEntityImpl<Long>{
        String name;
        String address;
        Parent parent;
    }

    @Test
    void allValuesSame_onlyNullEntityProperties_shouldBeEqual() throws Exception{
        //given
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("Meier");
        parent.setId(24L);

        Parent equalParent = new Parent(parent);
        //when
        boolean equal = comparator.isEqual(parent, equalParent);
        //then
        Assertions.assertTrue(equal);
    }

    @Test
    void allValuesSame_exceptOne_shouldNotBeEqual() throws Exception{
        //given
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("Meier");
        parent.setId(24L);

        Parent equalParent = new Parent(parent);
        equalParent.setName(parent.getName()+"MOD");
        //when
        boolean equal = comparator.isEqual(parent, equalParent);
        //then
        Assertions.assertFalse(equal);
    }

    @Test
    void allValuesSame_exceptEntityOnlyEqualById_shouldBeEqual() throws Exception{
        Child child = new Child();
        child.setId(42L);
        child.setName("child");
        //given
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("Meier");
        parent.setId(24L);

        Parent equalParent = new Parent(parent);
        parent.setChild(child);
        Child onlyEqualById = new Child();
        onlyEqualById.setId(child.getId());
        equalParent.setChild(onlyEqualById);

        //when
        boolean equal = comparator.isEqual(parent, equalParent);
        //then
        Assertions.assertTrue(equal);
    }
}