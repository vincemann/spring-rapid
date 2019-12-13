package io.github.vincemann.generic.crud.lib.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
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
     * @param startInstance
     * @param annotatedWith
     * @param checkCollections
     * @return
     * @throws IllegalAccessException
     */
    public static MultiValuedMap<Field,Object> getAllFieldsAnnotatedWith_WithoutThisField_OfAllMemberVars_AnnotatedWith(Object startInstance, Class<? extends Annotation> annotatedWith, boolean checkCollections, boolean emptyCollectionsIncluded) throws IllegalAccessException {
        MultiValuedMap<Field,Object> fields_instances_map = new ArrayListValuedHashMap<>();
        if(startInstance==null){
            throw new IllegalArgumentException("StartInstance must not be null");
        }
        if(!startInstance.getClass().isAnnotationPresent(annotatedWith)){
            throw new IllegalArgumentException("StartInstance must be annotated with: "+annotatedWith.getSimpleName());
        }
        List<Object> instancesChecked = new ArrayList<>();
        _getAllFieldsAnnotatedWith_WithoutThisField_OfAllMemberVars_DiveDownIfAnnotatedWith(startInstance,instancesChecked,fields_instances_map,annotatedWith,checkCollections,emptyCollectionsIncluded);
        return fields_instances_map;
    }

    public static MultiValuedMap<Field,Object> getAllFieldsAnnotatedWith_WithoutThisField_OfAllMemberVars_AnnotatedWith(Collection startCollection, Class<? extends Annotation> annotatedWith, boolean checkCollections, boolean emptyCollectionsIncluded) throws IllegalAccessException {
        if(startCollection==null){
            throw new IllegalArgumentException("StartCollection must not be null");
        }
        for (Object entry : startCollection) {
            /*if(entry==null){
                throw new IllegalArgumentException("StartCollection must not have null entries");
            }
            if(!entry.getClass().isAnnotationPresent(annotatedWith)){
                throw new IllegalArgumentException("StartCollection must have Entries annoted with: " + annotatedWith.getSimpleName());
            }*/
            return getAllFieldsAnnotatedWith_WithoutThisField_OfAllMemberVars_AnnotatedWith(entry,annotatedWith,checkCollections,emptyCollectionsIncluded);
        }
        throw new IllegalArgumentException("StartCollection must not be empty");
    }



    private static void _getAllFieldsAnnotatedWith_WithoutThisField_OfAllMemberVars_DiveDownIfAnnotatedWith(Object currentInstance,
                                                                                                            List<Object> instancesChecked,
                                                                                                            MultiValuedMap<Field,Object> fields_instances_map,
                                                                                                            Class<? extends Annotation> annotatedWith,
                                                                                                            boolean checkCollections,
                                                                                                            boolean emptyCollectionsIncluded)
            throws IllegalAccessException {
        //it is important to compare instances by reference and not by equals method
        if(ListUtils.containsByReference(instancesChecked,currentInstance)){
            //we already checked this instance -> done
            return;
        }
        instancesChecked.add(currentInstance);
        //save all fields of class
        Field[] fieldsOfInstance = getDeclaredFields_WithoutOutThisField(currentInstance.getClass(), true);

        //now we check for memberVars, that we have to dive into
        for (Field field : fieldsOfInstance) {
            if(field.getType().isAnnotationPresent(annotatedWith)) {
                fields_instances_map.put(field,currentInstance);
                //we need to dive into this memberVar
                field.setAccessible(true);
                Object memberVar = field.get(currentInstance);
                if(memberVar!=null) {
                    _getAllFieldsAnnotatedWith_WithoutThisField_OfAllMemberVars_DiveDownIfAnnotatedWith(memberVar,instancesChecked, fields_instances_map, annotatedWith,checkCollections,emptyCollectionsIncluded);
                }
            }
            else if(checkCollections){
                if(Collection.class.isAssignableFrom(field.getType())){
                    field.setAccessible(true);
                    Collection memberCollection = (Collection) field.get(currentInstance);
                    if(memberCollection!=null) {
                        //this boolean is introduced to prevent duplicates
                        if(emptyCollectionsIncluded) {
                            if (memberCollection.isEmpty()){
                                fields_instances_map.put(field, currentInstance);
                            }
                        }
                        boolean mapEntrySaved = false;
                        for (Object entry : memberCollection) {
                            //we need to dive into this memberVar, which is part of a collection
                            if (entry != null) {
                                if (entry.getClass().isAnnotationPresent(annotatedWith)) {
                                    if(!mapEntrySaved) {
                                        fields_instances_map.put(field, currentInstance);
                                        mapEntrySaved=true;
                                    }
                                    _getAllFieldsAnnotatedWith_WithoutThisField_OfAllMemberVars_DiveDownIfAnnotatedWith(entry, instancesChecked, fields_instances_map, annotatedWith, checkCollections,emptyCollectionsIncluded);
                                }
                            }
                        }
                    }
                }
            }
        }
    }




}