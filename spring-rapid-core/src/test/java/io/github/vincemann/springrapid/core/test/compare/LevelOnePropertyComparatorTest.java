package io.github.vincemann.springrapid.core.test.compare;

import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;
import java.util.Set;

class LevelOnePropertyComparatorTest {

    LevelOnePropertyComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new LevelOnePropertyComparator();
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    class Parent extends IdentifiableEntityImpl<Long> {
        String name;
        int age;
        LevelOneFullComparatorTest.Child child;
        Set<LevelOneFullComparatorTest.Child> childSet;

        public Parent() {
        }

        public Parent(LevelOneFullComparatorTest.Parent copy){
            this(copy.name,copy.age,copy.child,copy.childSet);
            this.setId(copy.getId());
        }
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    class NoEntity{
        private String name;
        private int age;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    class NoEntityTwo{
        private String name;
        private int age;
        private String description;
    }



    @Entity
    @Getter
    @Setter
    class Child extends IdentifiableEntityImpl<Long>{
        String name;
        String address;
        LevelOneFullComparatorTest.Parent parent;

        public Child() {
        }
    }

    @Test
    public void entity_onlyCheckedPropertyEqual_onlyCheckThis_shouldBeConsideredEqual_sameType(){
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
    public void entity_selectedPropertiesNotEqual_sameType_shouldBeConsideredNotEqual(){
        Parent parent = new Parent();
        parent.setAge(43);
        parent.setName("meier");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("meier");

        comparator.includeProperty(parent::getAge);

        Assertions.assertFalse(comparator.isEqual(parent,second));
    }

    @Test
    public void entity_selectedPropertiesEqual_diffType_shouldBeConsideredEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("meier");

        Child child = new Child();
        child.setName("meier");

        comparator.includeProperty(parent::getName);
        Assertions.assertTrue(comparator.isEqual(parent,child));
    }

    @Test
    public void entity_selectedPropertiesNotEqual_diffType_shouldBeConsideredNotEqual(){
        //todo fails bc diff type ergibt immer equal... außer ich vergleiche id??
        Parent parent = new Parent();
        //parent.setId(1L);
        parent.setAge(42);
        parent.setName("meierDiff");

        Child child = new Child();
        //child.setId(2L);
        child.setName("meier");

        comparator.includeProperty(parent::getName);


        Assertions.assertFalse(comparator.isEqual(parent,child));
    }

    @Test
    public void entity_selectedPropertyNotEqual_idsEqual_diffType_shouldBeConsideredNotEqual(){
        //todo mit id unterschiedlich klappt es dann obwohl id gar nicht im scope des vergleichs ist..
        Parent parent = new Parent();
        parent.setId(1L);
        parent.setAge(42);
        parent.setName("meierDiff");

        Child child = new Child();
        child.setId(1L);
        child.setName("meier");

        comparator.includeProperty(parent::getName);


        Assertions.assertFalse(comparator.isEqual(parent,child));
    }

    @Test
    public void selectedPropertiesNotEqual_diffType_shouldBeConsideredNotEqual(){
        //todo fails bc diff type ergibt immer equal... außer es gibt eine id, dann nimmt er die zum vergleich..
        NoEntity parent = new NoEntity();
        parent.setAge(42);
        parent.setName("meierDiff");

        NoEntityTwo child = new NoEntityTwo();
        child.setName("meier");
        child.setAge(42);

        comparator.includeProperty(parent::getName);


        Assertions.assertFalse(comparator.isEqual(parent,child));
    }

    @Test
    public void selectedPropertiesNotEqual_sameType_shouldBeConsideredNotEqual(){
        NoEntity parent = new NoEntity();
        parent.setAge(42);
        parent.setName("meierDiff");

        NoEntity child = new NoEntity();
        child.setName("meier");
        child.setAge(42);

        comparator.includeProperty(parent::getAge);


        Assertions.assertTrue(comparator.isEqual(parent,child));
    }

    @Test
    public void selectedPropertiesEqual_diffType_shouldBeConsideredEqual(){
        NoEntity parent = new NoEntity();
        parent.setAge(42);
        parent.setName("meierDiff");

        NoEntityTwo child = new NoEntityTwo();
        child.setName("meier");
        child.setAge(42);

        comparator.includeProperty(parent::getAge);


        Assertions.assertTrue(comparator.isEqual(parent,child));
    }



    @Test
    public void entity_onlyCheckedPropertyUnEqual_onlyCheckThis_shouldBeConsideredUnEqual(){
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