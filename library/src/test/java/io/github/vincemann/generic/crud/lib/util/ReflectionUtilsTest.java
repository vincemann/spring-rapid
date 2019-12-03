package io.github.vincemann.generic.crud.lib.util;

import lombok.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import java.lang.reflect.Field;
import java.util.*;

class ReflectionUtilsTest {

    //Works also if superclass is not abstract
    @AllArgsConstructor
    @Entity
    @NoArgsConstructor
    @Getter
    @Setter
    private abstract class Person{
        int height;
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class Parent extends Person{
        private String name;
        private int telephoneNr;
        @OneToMany
        private Set<Child> children;
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class Child{
        private String name;
        private Date born;
        private String fieldWithNullValue;
        @OneToMany
        private Set<GrandChild> grandChildren;
        @ManyToOne
        private Parent parent;
        private NoEntity noEntity;
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class GrandChild{
        private String readme;
        private Set<NoEntity> noEntities;
        private Set<NoEntity> nullElementList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class NoEntity{
        private String shouldNotFind;
        private int pleaseNotFindThis;
    }


    @Test
    void getAllFields_WithoutThisField_OfAllMemberVars_AnnotatedWith() throws IllegalAccessException {
//GIVEN
        GrandChild grandChild1 = new GrandChild();
        grandChild1.setReadme("i am a cool readme");
        grandChild1.setNoEntities(new HashSet<>(Arrays.asList(new NoEntity("dont",12))));
        grandChild1.setNullElementList(new HashSet<>(Arrays.asList(null,null)));

        GrandChild grandChild2 = new GrandChild();
        grandChild2.setReadme("i am a cool readme2");

        Date date = new Date(1998, Calendar.JANUARY,11);
        Child child = new Child();
        child.setBorn(date);
        child.setGrandChildren(new HashSet<>(Arrays.asList(grandChild1,grandChild2)));
        child.setNoEntity(new NoEntity("please dont find",99));
        child.setFieldWithNullValue(null);

        Parent parent = new Parent();
        parent.setHeight(182);
        parent.setName("grandPArent");
        parent.setTelephoneNr(123);
        parent.setChildren(Collections.singleton(child));
//WHEN
        Set<Field> fields = ReflectionUtils.getAllFields_WithoutThisField_OfAllMemberVars_AnnotatedWith(parent, Entity.class,true);
//THEN
        for (Field field : fields) {
            System.out.println(field.getName());
        }
        Assertions.assertEquals(13,fields.size());
        //Assert that NoEntity instance was not dived into, because it is not of type Entity
        Assertions.assertFalse(fields.stream().anyMatch(field -> field.getName().equals("shouldNotFind")));
        Assertions.assertFalse(fields.stream().anyMatch(field -> field.getName().equals("pleaseNotFindThis")));
        //Assert that fields with null values are found
        Assertions.assertTrue(fields.stream().anyMatch(field -> field.getName().equals("fieldWithNullValue")));
        //Assert that readme Field of GrandChild was only found once, although there were two instances of this class
        Assertions.assertEquals(1,fields.stream().filter(field -> field.getName().equals("readme")).count());
        Assertions.assertEquals(1,fields.stream().filter(field -> field.getName().equals("nullElementList")).count());
    }
}