package io.github.vincemann.generic.crud.lib.test.deepCompare;

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

import static org.junit.jupiter.api.Assertions.*;

class IgnoreIdEntityReflectionComparatorTest {

    ReflectionComparator reflectionComparator;

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

    @BeforeEach
    void setUp() {
        reflectionComparator = EntityReflectionComparator.IGNORE_ID();
    }

    @Test
    void differentValues_shouldNotBeEqual(){
        //given
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("Meier");
        parent.setId(24L);

        Parent equalParent = new Parent(parent);
        equalParent.setAge(1);

        boolean equal = reflectionComparator.isEqual(parent, equalParent);
        Assertions.assertFalse(equal);
    }

    @Test
    void onlyIdsDiffer_shouldStillBeEqual() {
        //given
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("Meier");
        parent.setId(24L);

        Parent equalParent = new Parent(parent);
        equalParent.setId(1L);

        boolean equal = reflectionComparator.isEqual(parent, equalParent);
        Assertions.assertTrue(equal);
    }
}