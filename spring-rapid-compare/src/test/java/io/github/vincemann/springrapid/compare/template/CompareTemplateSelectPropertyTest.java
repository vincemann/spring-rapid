package io.github.vincemann.springrapid.compare.template;

import com.github.hervian.reflection.Types;
import lombok.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.github.vincemann.springrapid.compare.template.CompareTemplate.compare;

class CompareTemplateSelectPropertyTest {



    @Getter
    @Setter
    @AllArgsConstructor
    class Parent  {
        long id;
        String name;
        int age;
        CompareTemplateFullEqualTest.Child child;
        Set<CompareTemplateFullEqualTest.Child> childSet;

        public Parent() {
        }

        public Parent(CompareTemplateFullEqualTest.Parent copy){
            this(copy.id,copy.name,copy.age,copy.child,copy.childSet);
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



    @Getter
    @Setter
    class Child {
        long id;
        String name;
        String address;
        CompareTemplateFullEqualTest.Parent parent;

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

        boolean equal = compareWithProperty(parent,second,parent::getAge);

        Assertions.assertTrue(equal);
    }

    @Test
    public void entity_selectedPropertiesNotEqual_sameType_shouldBeConsideredNotEqual(){
        Parent parent = new Parent();
        parent.setAge(43);
        parent.setName("meier");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("meier");

        boolean equal = compareWithProperty(parent,second,parent::getAge);

        Assertions.assertFalse(equal);
    }

    @Test
    public void entity_selectedPropertiesEqual_diffType_shouldBeConsideredEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("meier");

        Child child = new Child();
        child.setName("meier");

        boolean equal = compareWithProperty(parent,child,parent::getName);
        Assertions.assertTrue(equal);
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

        boolean equal = compareWithProperty(parent,child,parent::getName);


        Assertions.assertFalse(equal);
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

        boolean equal = compareWithProperty(parent,child,parent::getName);

        Assertions.assertFalse(equal);
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

        boolean equal = compareWithProperty(parent,child,parent::getName);

        Assertions.assertFalse(equal);
    }

    @Test
    public void selectedPropertiesNotEqual_sameType_shouldBeConsideredNotEqual(){
        NoEntity parent = new NoEntity();
        parent.setAge(42);
        parent.setName("meierDiff");

        NoEntity child = new NoEntity();
        child.setName("meier");
        child.setAge(42);

        boolean equal = compareWithProperty(parent,child,parent::getAge);


        Assertions.assertTrue(equal);
    }

    @Test
    public void selectedPropertiesEqual_diffType_shouldBeConsideredEqual(){
        NoEntity parent = new NoEntity();
        parent.setAge(42);
        parent.setName("meierDiff");

        NoEntityTwo child = new NoEntityTwo();
        child.setName("meier");
        child.setAge(42);

        boolean equal = compareWithProperty(parent,child,parent::getAge);

        Assertions.assertTrue(equal);
    }



    @Test
    public void entity_onlyCheckedPropertyUnEqual_onlyCheckThis_shouldBeConsideredUnEqual(){
        Parent parent = new Parent();
        parent.setAge(42);
        parent.setName("meier");

        Parent second = new Parent();
        second.setAge(42);
        second.setName("junker");

        boolean equal = compareWithProperty(parent,second,parent::getName);

        Assertions.assertFalse(equal);

    }

    private boolean compareWithProperty(Object root, Object compare, Types.Supplier<?> getter){
        return compare(root)
                .with(compare)
                .properties()
                .include(getter)
                .isEqual();
    }
}