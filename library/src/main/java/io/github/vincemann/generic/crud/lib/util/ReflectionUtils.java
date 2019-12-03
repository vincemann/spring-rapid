package io.github.vincemann.generic.crud.lib.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kupal 3kb
 */
@Slf4j
public class ReflectionUtils {

    /**
     * Create new instance of specified class and type
     *
     * @param clazz of instance
     * @param <T>   type of object
     * @return new Class instance
     */
    public static <T> T getInstance(Class<T> clazz) {
        T t = null;
        try {
            t = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return t;
    }


    public static List<Method> findGetters(Class clazz){
        List<Method> getters = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
                getters.add(method);
            }
        }
        return getters;
    }
    /**
     * Retrieving fields list of specified class
     * If recursively is true, retrieving fields from all class hierarchy
     *
     * @param clazz       where fields are searching
     * @param recursively param
     * @return list of fields
     */
    public static Field[] getDeclaredFields(Class clazz, boolean recursively) {
        List<Field> fields = new LinkedList<Field>();
        Field[] declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields, declaredFields);

        Class superClass = clazz.getSuperclass();

        if (superClass != null && recursively) {
            Field[] declaredFieldsOfSuper = getDeclaredFields(superClass, recursively);
            if (declaredFieldsOfSuper.length > 0)
                Collections.addAll(fields, declaredFieldsOfSuper);
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Retrieving fields list of specified class
     * If recursively is true, retrieving fields from all class hierarchy
     *
     * @param clazz       where fields are searching
     * @param recursively param
     * @return list of fields
     */
    public static Field[] getDeclaredFields_WithoutOutThisField(Class clazz, boolean recursively) {
        List<Field> fields = new LinkedList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields, declaredFields);
        //remove "This" Field
        List<Field> thisFields = fields.stream().filter(field -> field.getName().startsWith("this$")).collect(Collectors.toList());
        fields.removeAll(thisFields);

        Class superClass = clazz.getSuperclass();

        if (superClass != null && recursively) {
            Field[] declaredFieldsOfSuper = getDeclaredFields_WithoutOutThisField(superClass, recursively);
            if (declaredFieldsOfSuper.length > 0)
                Collections.addAll(fields, declaredFieldsOfSuper);
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Retrieving fields list of specified class and which
     * are annotated by incoming annotation class
     * If recursively is true, retrieving fields from all class hierarchy
     *
     * @param clazz           - where fields are searching
     * @param annotationClass - specified annotation class
     * @param recursively     param
     * @return list of annotated fields
     */
    public static Field[] getAnnotatedDeclaredFields(Class clazz,
                                                     Class<? extends Annotation> annotationClass,
                                                     boolean recursively) {
        Field[] allFields = getDeclaredFields(clazz, recursively);
        List<Field> annotatedFields = new LinkedList<Field>();

        for (Field field : allFields) {
            if (field.isAnnotationPresent(annotationClass))
                annotatedFields.add(field);
        }

        return annotatedFields.toArray(new Field[annotatedFields.size()]);
    }

    /**
     * Retrieving fields list of specified class and which
     * are annotated by incoming annotation class
     * If recursively is true, retrieving fields from all class hierarchy
     *
     * @param clazz           - where fields are searching
     * @param annotationClass - specified annotation class
     * @param recursively     param
     * @return list of annotated fields
     */
    public static Field[] getAnnotatedDeclaredFieldsAssignableFrom(Class clazz,
                                                                   Class<? extends Annotation> annotationClass,
                                                                   Class interfaceThatNeedsToBeImplemented,
                                                                   boolean recursively) {
        Field[] allFields = getDeclaredFields(clazz, recursively);
        List<Field> annotatedFields = new LinkedList<Field>();

        for (Field field : allFields) {
            if (field.isAnnotationPresent(annotationClass)) {
                if (interfaceThatNeedsToBeImplemented.isAssignableFrom(field.getType())) {
                    annotatedFields.add(field);
                }
            }
        }

        return annotatedFields.toArray(new Field[annotatedFields.size()]);
    }

    public static Field[] getDeclaredFieldsAnnotatedWith(Class clazz, Class<? extends Annotation> annotationClass, boolean recursively) {
        /*Field[] allFields = getDeclaredFields(clazz, recursively);
        List<Field> annotatedFields = new LinkedList<Field>();

        for (Field field : allFields) {
            if (field.isAnnotationPresent(annotationClass)) {
                annotatedFields.add(field);
            }
        }

        return annotatedFields.toArray(new Field[annotatedFields.size()]);*/
        return FieldUtils.getFieldsWithAnnotation(clazz,annotationClass);
    }

    public static Field[] getDeclaredFieldsAssignableFrom(Class clazz, Class interfaceThatNeedsToBeImplemented, boolean recursively) {
        Field[] allFields = getDeclaredFields(clazz, recursively);
        List<Field> annotatedFields = new LinkedList<Field>();

        for (Field field : allFields) {
            if (interfaceThatNeedsToBeImplemented.isAssignableFrom(field.getType())) {
                annotatedFields.add(field);
            }
        }
        return annotatedFields.toArray(new Field[annotatedFields.size()]);
    }

    public static Field[] getAllFieldsAssignableFrom(Class clazz, Class interfaceThatNeedsToBeImplemented, boolean recursively) {
        Field[] allFields = getDeclaredFields(clazz, recursively);
        List<Field> annotatedFields = new LinkedList<Field>();

        for (Field field : allFields) {
            if (interfaceThatNeedsToBeImplemented.isAssignableFrom(field.getType())) {
                annotatedFields.add(field);
            }
        }
        return annotatedFields.toArray(new Field[annotatedFields.size()]);
    }

    /**
     * Starts with given Instance.
     * Save All fields of instance class without duplicates. (The "this" Fields of the classes wont be added)
     * Checks all member Variables of instance.
     * If it finds a MemberVariable which is of Type annotated with {@code annotatedWith},
     * then this instance will be "dived into" aka start with step 1 again.
     *
     * @param instance
     * @param annotatedWith
     * @param checkCollections
     * @return
     * @throws IllegalAccessException
     */
    public static Set<Field> getAllFields_WithoutThisField_OfAllMemberVars_AnnotatedWith(Object instance, Class<? extends Annotation> annotatedWith, boolean checkCollections) throws IllegalAccessException {
        Set<Field> fields = new HashSet<>();
        if(instance==null){
            throw new IllegalArgumentException("Instance must not be null");
        }
        List<Class> classesChecked = new ArrayList<>();
        _getAllFields_WithoutThisField_OfAllMemberVars_DiveDownIfAnnotatedWith(instance,classesChecked,fields,annotatedWith,checkCollections);
        return fields;
    }

    private static void _getAllFields_WithoutThisField_OfAllMemberVars_DiveDownIfAnnotatedWith(Object instance,
                                                                                               List<Class> classesChecked,
                                                                                               Set<Field> fields,
                                                                                               Class<? extends Annotation> annotatedWith,
                                                                                               boolean checkCollections)
            throws IllegalAccessException {
        if(classesChecked.contains(instance.getClass())){
            //we already checked this class -> done
            return;
        }
        classesChecked.add(instance.getClass());
        //save all fields of class
        Field[] instanceFields = getDeclaredFields_WithoutOutThisField(instance.getClass(), true);
        //we are preventing duplicates by using a set
        fields.addAll(Arrays.asList(instanceFields));

        //now we check for memberVars, that we have to dive into
        for (Field instanceField : instanceFields) {
            if(instanceField.getType().isAnnotationPresent(annotatedWith)) {
                //we need to dive into this memberVar
                instanceField.setAccessible(true);
                Object memberVar = instanceField.get(instance);
                if(memberVar!=null) {
                    _getAllFields_WithoutThisField_OfAllMemberVars_DiveDownIfAnnotatedWith(memberVar,classesChecked, fields, annotatedWith,checkCollections);
                }
            }
            else if(checkCollections){
                if(Collection.class.isAssignableFrom(instanceField.getType())){
                    instanceField.setAccessible(true);
                    Collection memberCollection = (Collection) instanceField.get(instance);
                    for (Object entry : memberCollection) {
                        //we need to dive into this memberVar, which is part of a collection
                        if(entry!=null) {
                            if (entry.getClass().isAnnotationPresent(annotatedWith)) {
                                _getAllFields_WithoutThisField_OfAllMemberVars_DiveDownIfAnnotatedWith(entry, classesChecked, fields, annotatedWith, checkCollections);
                            }
                        }
                    }
                }
            }
        }
    }


}