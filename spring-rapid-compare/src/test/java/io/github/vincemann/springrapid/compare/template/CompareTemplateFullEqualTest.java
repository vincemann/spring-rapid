package io.github.vincemann.springrapid.compare.template;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.github.vincemann.springrapid.compare.template.CompareTemplate.compare;

class CompareTemplateFullEqualTest {


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    class Parent {
        long id;
        String name;
        int age;
        Child child;
        Set<Child> childSet;

        public Parent(Parent copy){
            this(copy.getId(), copy.name,copy.age,copy.child,copy.childSet);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    class Child {
        long id;
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
        boolean equal = fullEqualCompare(parent, equalParent);
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
        boolean equal = fullEqualCompare(parent, equalParent);
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
        boolean equal = fullEqualCompare(parent, equalParent);
        //then
        Assertions.assertTrue(equal);
    }

    public boolean fullEqualCompare(Object root, Object compare){
        return compare(root)
                .with(compare)
                .properties()
                .all()
                .isEqual();
    }
}