package io.github.vincemann.generic.crud.lib.util;

import lombok.*;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


class ReflectionUtilsTest {

    @AllArgsConstructor
    @Entity
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
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
        @OneToMany
        private Set<Child> children;

        @Override
        public String toString() {
            return "Parent{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class Child extends Person{
        private String name;
        private String fieldWithNullValue;
        @OneToMany
        private Set<GrandChild> grandChildren;
        @ManyToOne
        private Parent parent;
        private NoEntity noEntity;

        @Override
        public String toString() {
            return "Child{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class GrandChild extends  Person{
        private String name;
        private Set<NoEntity> noEntities;
        private Set<NoEntity> nullElementList;
        @OneToMany
        private Set<LastEntity> lastEntities;

        @Override
        public String toString() {
            return "GrandChild{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class LastEntity extends Person{
        private String iAmTheLastNote;
        @ManyToOne
        private GrandChild grandChild;

        @Override
        public String toString() {
            return "LastEntity{" +
                    "iAmTheLastNote='" + iAmTheLastNote + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    private class NoEntity{
        private String shouldNotFind;
        private int pleaseNotFindThis;
    }


    @Test
    void getAllFields_WithoutThisField_OfAllMemberVars_AnnotatedWith() throws IllegalAccessException, NoSuchFieldException {
//GIVEN
        GrandChild grandChild1 = new GrandChild();
        grandChild1.setName("grandchild 1");
        grandChild1.setNoEntities(new HashSet<>(Arrays.asList(new NoEntity("dont",12))));
        grandChild1.setNullElementList(new HashSet<>(Arrays.asList(null,null)));

        GrandChild grandChild2 = new GrandChild();
        grandChild2.setName("grandchild2");

        Child child = new Child();
        child.setName("child");
        child.setGrandChildren(new HashSet<>(Arrays.asList(grandChild1,grandChild2)));
        child.setNoEntity(new NoEntity("please dont find",99));
        child.setFieldWithNullValue(null);

        Parent parent = new Parent();
        parent.setHeight(182);
        parent.setName("parent");
        parent.setChildren(Collections.singleton(child));

        LastEntity lastEntity1 = new LastEntity();
        lastEntity1.setIAmTheLastNote("i am 1");
        lastEntity1.setHeight(1);
        lastEntity1.setGrandChild(grandChild1);

        LastEntity lastEntity2 = new LastEntity();
        lastEntity2.setIAmTheLastNote("i am 2");
        lastEntity2.setHeight(2);
        lastEntity2.setGrandChild(grandChild2);

        LastEntity lastEntity3 = new LastEntity();
        lastEntity3.setIAmTheLastNote("i am 3");
        lastEntity3.setHeight(3);
        lastEntity3.setGrandChild(grandChild1);

        grandChild1.setLastEntities(new HashSet<>(Arrays.asList(lastEntity1,lastEntity3)));
        grandChild2.setLastEntities(new HashSet<>(Arrays.asList(lastEntity2)));
//WHEN
        MultiValuedMap<Field, Object> field_instances_map =
                ReflectionUtils.getAllFieldsAnnotatedWith_WithoutThisField_OfAllMemberVars_AnnotatedWith(parent, Entity.class, true,false);
//THEN
        Collection<Map.Entry<Field, Object>> entries = field_instances_map.entries();
        for (Map.Entry<Field, Object> entry : entries) {
            System.out.println("entry: "+" fieldclass: "+entry.getKey().getDeclaringClass().getSimpleName()+", fieldname:" + entry.getKey().getName() + ", instance:" + entry.getValue().toString());
        }
        Assertions.assertEquals(8,field_instances_map.size());

        //have all instances of type Entity been found in the entityGraph?
        Collection<Object> instances = field_instances_map.values();
        Assertions.assertTrue(instances.contains(parent));
        Assertions.assertTrue(instances.contains(child));
        Assertions.assertTrue(instances.contains(grandChild1));
        Assertions.assertTrue(instances.contains(grandChild2));
        Assertions.assertTrue(instances.contains(lastEntity1));
        Assertions.assertTrue(instances.contains(lastEntity2));
        Assertions.assertTrue(instances.contains(lastEntity3));

        //have all expected fields been found
        MultiSet<Field> keys = field_instances_map.keys();
        Assertions.assertTrue(keys.contains(Parent.class.getDeclaredField("children")));
        Assertions.assertTrue(keys.contains(Child.class.getDeclaredField("parent")));
        Assertions.assertTrue(keys.contains(Child.class.getDeclaredField("grandChildren")));
        Assertions.assertEquals(2,keys.stream().filter(field -> field.getName().equals("lastEntities")).count());
        Assertions.assertEquals(3,keys.stream().filter(field -> field.getName().equals("grandChild")).count());

        //check if the right instances were mapped to the fields
        //to field with name children, parent instance must be mapped
        List<Map.Entry<Field, Object>> childEntriesFoundByFieldName = entries.stream().filter(entry -> entry.getKey().getName().equals("children")).collect(Collectors.toList());
        Assertions.assertEquals(1,childEntriesFoundByFieldName.size());
        Map.Entry<Field, Object> childEntry = childEntriesFoundByFieldName.get(0);
        Assertions.assertEquals(parent,childEntry.getValue());

        //to field with name parent, child instance must be mapped
        List<Map.Entry<Field, Object>> parentEntriesFoundByFieldName = entries.stream().filter(entry -> entry.getKey().getName().equals("parent")).collect(Collectors.toList());
        Assertions.assertEquals(1,parentEntriesFoundByFieldName.size());
        Map.Entry<Field, Object> parentEntry = parentEntriesFoundByFieldName.get(0);
        Assertions.assertEquals(child,parentEntry.getValue());

        //to field with name grandChildren, child instance must be mapped
        List<Map.Entry<Field, Object>> grandChildrenEntriesFoundByFieldName = entries.stream().filter(entry -> entry.getKey().getName().equals("grandChildren")).collect(Collectors.toList());
        Assertions.assertEquals(1,grandChildrenEntriesFoundByFieldName.size());
        Map.Entry<Field, Object> grandChildEntry1 = grandChildrenEntriesFoundByFieldName.get(0);
        Assertions.assertEquals(child,grandChildEntry1.getValue());

        //to field with name lastEntities, grandchild1 and grandchild2  instances must be mapped (either the one or the other)
        List<Map.Entry<Field, Object>> lastEntitiesEntriesFoundByFieldName = entries.stream().filter(entry -> entry.getKey().getName().equals("lastEntities")).collect(Collectors.toList());
        Assertions.assertEquals(2,lastEntitiesEntriesFoundByFieldName.size());
        Assertions.assertEquals(1,lastEntitiesEntriesFoundByFieldName.stream().filter(entry -> entry.getValue().equals(grandChild1)).count());
        Assertions.assertEquals(1,lastEntitiesEntriesFoundByFieldName.stream().filter(entry -> entry.getValue().equals(grandChild2)).count());


        //to field with name grandChild, lastE1 and lastE2 and LastE3 instances must be mapped (all three must be mapped once)
        List<Map.Entry<Field, Object>> grandChildEntriesFoundByFieldName = entries.stream().filter(entry -> entry.getKey().getName().equals("grandChild")).collect(Collectors.toList());
        Assertions.assertEquals(3,grandChildEntriesFoundByFieldName.size());
        Assertions.assertEquals(1,grandChildEntriesFoundByFieldName.stream().filter(entry -> entry.getValue().equals(lastEntity1)).count());
        Assertions.assertEquals(1,grandChildEntriesFoundByFieldName.stream().filter(entry -> entry.getValue().equals(lastEntity2)).count());
        Assertions.assertEquals(1,grandChildEntriesFoundByFieldName.stream().filter(entry -> entry.getValue().equals(lastEntity3)).count());
    }


}