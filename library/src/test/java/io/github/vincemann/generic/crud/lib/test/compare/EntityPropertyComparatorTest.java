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

import static org.junit.jupiter.api.Assertions.*;

class EntityPropertyComparatorTest {

    EntityPropertyComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new EntityPropertyComparator();
    }

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    class Parent extends IdentifiableEntityImpl<Long> {
        String name;
        int age;
        EqualsForEntitiesEntityFullComparatorTest.Child child;
        Set<EqualsForEntitiesEntityFullComparatorTest.Child> childSet;

        public Parent(EqualsForEntitiesEntityFullComparatorTest.Parent copy){
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
        EqualsForEntitiesEntityFullComparatorTest.Parent parent;
    }

    @Test
    public void onlyCheckedPropertyEqual_onlyCheckThis_shouldBeConsideredEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("meier");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("junker");

        comparator.includeProperty(parent::getAge);

        Assertions.assertTrue(comparator.isEqual(parent,second));

    }

    @Test
    public void onlyCheckedPropertyUnEqual_onlyCheckThis_shouldBeConsideredUnEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("meier");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("junker");

        comparator.includeProperty(parent::getName);

        Assertions.assertFalse(comparator.isEqual(parent,second));

    }
}