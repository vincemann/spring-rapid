package io.github.vincemann.springrapid.core.util;


import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.reflect.FieldUtils;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class ReflectionUtils {

//    public static <T> T getInstance(Class<T> clazz) {
//        T t = null;
//        try {
//            t = clazz.newInstance();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        return t;
//    }


    public static List<Method> findGetters(Class clazz) {
        List<Method> getters = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
                getters.add(method);
            }
        }
        return getters;
    }

    /**
     * Retrieving fields list of specified class.
     * If @param recursively is true, retrieving fields from whole class hierarchy
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

    public static Map<String,Field> getNonStaticFieldMap(Class<?> clazz){
        Map<String,Field> fieldMap = new HashMap<>();
        for (Field entityField : Sets.newHashSet(ReflectionUtils.getDeclaredFields(clazz, true))) {
            if (!Modifier.isStatic(entityField.getModifiers())) {
                fieldMap.put(entityField.getName(), entityField);
            }
        }
        return fieldMap;
    }

    /**
     * Same as {@link this#getDeclaredFields(Class, boolean)} but the 'this' field is excluded.
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
     * Same as {@link this#getDeclaredFields(Class, boolean)} but only field annotated with @param annotationClass
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
     * Same as {@link this#getDeclaredFieldsAnnotatedWith(Class, Class, boolean)} but field also need to be assignable from @param interfaceThatNeedsToBeImplemented.
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
        return FieldUtils.getFieldsWithAnnotation(clazz, annotationClass);
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
     * @param root
     * @param annotatedWith
     * @param checkCollections
     * @return
     * @throws IllegalAccessException
     */
    public static MultiValuedMap<Field, Object> findFieldsAndTheirDeclaringInstances_OfAllMemberVars_AnnotatedWith(
            Object root,
            Class<? extends Annotation> annotatedWith,
            boolean checkCollections,
            boolean emptyCollectionsIncluded)
            throws IllegalAccessException {
        MultiValuedMap<Field, Object> fields_instances_map = new ArrayListValuedHashMap<>();
        if (root == null) {
            throw new IllegalArgumentException("StartInstance must not be null");
        }

        List<Object> instancesChecked = new ArrayList<>();
        if(Collection.class.isAssignableFrom(root.getClass())){
            for (Object entry : ((Collection) root)) {
                if(entry==null){
                    //skip
                    continue;
                }
                return findFieldsAndTheirDeclaringInstances_OfAllMemberVars_AnnotatedWith(entry, annotatedWith, checkCollections, emptyCollectionsIncluded);
            }
            return fields_instances_map;
        }else {
            if (!root.getClass().isAnnotationPresent(annotatedWith)) {
                throw new IllegalArgumentException("StartInstance must be annotated with: " + annotatedWith.getSimpleName());
            }
            _findFieldsAndTheirDeclaringInstances_OfAllMemberVars_AnnotatedWith(root, instancesChecked, fields_instances_map, annotatedWith, checkCollections, emptyCollectionsIncluded);
        }

        return fields_instances_map;
    }

    private static void _findFieldsAndTheirDeclaringInstances_OfAllMemberVars_AnnotatedWith(Object currentInstance,
                                                                                            List<Object> instancesChecked,
                                                                                            MultiValuedMap<Field, Object> fields_instances_map,
                                                                                            Class<? extends Annotation> annotatedWith,
                                                                                            boolean checkCollections,
                                                                                            boolean emptyCollectionsIncluded)
            throws IllegalAccessException {
        //it is important to compare instances by reference and not by equals method
        if (ListUtils.containsByReference(instancesChecked, currentInstance)) {
            //we already checked this instance -> done
            return;
        }
        instancesChecked.add(currentInstance);
        //save all fields of class
        Field[] fieldsOfInstance = getDeclaredFields_WithoutOutThisField(currentInstance.getClass(), true);

        //now we check for memberVars, that we have to dive into
        for (Field field : fieldsOfInstance) {
            if (field.getType().isAnnotationPresent(annotatedWith)) {
                fields_instances_map.put(field, currentInstance);
                field.setAccessible(true);
                Object memberVar = field.get(currentInstance);
                if (memberVar != null) {
                    //we need to dive into this memberVar
                    _findFieldsAndTheirDeclaringInstances_OfAllMemberVars_AnnotatedWith(memberVar, instancesChecked, fields_instances_map, annotatedWith, checkCollections, emptyCollectionsIncluded);
                }
            } else if (checkCollections) {
                if (Collection.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    Collection memberCollection = (Collection) field.get(currentInstance);
                    if (memberCollection != null) {
                        if (emptyCollectionsIncluded) {
                            if (memberCollection.isEmpty()) {
                                fields_instances_map.put(field, currentInstance);
                            }
                        }
                        //this boolean is introduced to prevent duplicates
                        boolean mapEntrySaved = false;
                        for (Object entry : memberCollection) {
                            if (entry != null) {
                                if (entry.getClass().isAnnotationPresent(annotatedWith)) {
                                    //we need to dive into this memberVar, which is part of a collection
                                    if (!mapEntrySaved) {
                                        fields_instances_map.put(field, currentInstance);
                                        mapEntrySaved = true;
                                    }
                                    _findFieldsAndTheirDeclaringInstances_OfAllMemberVars_AnnotatedWith(entry, instancesChecked, fields_instances_map, annotatedWith, checkCollections, emptyCollectionsIncluded);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static Set<Object> findObjects_OfAllMemberVars_AssignableFrom(Object root,
                                                                    Class<?> assignableFrom,
                                                                    boolean checkCollections) throws IllegalAccessException {
        if (root == null) {
            throw new IllegalArgumentException("StartInstance must not be null");
        }

        List<Object> instancesChecked = new ArrayList<>();
        Set<Object> result = new HashSet();
        if(Collection.class.isAssignableFrom(root.getClass())){
            for (Object entry : ((Collection) root)) {
                if(entry==null){
                    //skip
                    continue;
                }
                _findObjects_OfAllMemberVars_AssignableFrom(entry,assignableFrom,checkCollections,result,instancesChecked);
            }
        }else {
            if(assignableFrom.isAssignableFrom(root.getClass())){
                result.add(root);
            }
            _findObjects_OfAllMemberVars_AssignableFrom(root,assignableFrom,checkCollections,result,instancesChecked);
        }

        return result;
    }

    private static void _findObjects_OfAllMemberVars_AssignableFrom(Object currentInstance,
                                                                   Class<?> assignableFrom,
                                                                   boolean checkCollections,
                                                                   Set<Object> objectsFound,
                                                                   List<Object> instancesChecked) throws IllegalAccessException {
        //it is important to compare instances by reference and not by equals method
        if (ListUtils.containsByReference(instancesChecked, currentInstance)) {
            //we already checked this instance -> done
            return;
        }
        instancesChecked.add(currentInstance);
        //save all fields of class
        Field[] fieldsOfInstance = getDeclaredFields_WithoutOutThisField(currentInstance.getClass(), true);

        //now we check for memberVars, that we have to dive into
        for (Field field : fieldsOfInstance) {
            if (assignableFrom.isAssignableFrom(field.getType())) {

                field.setAccessible(true);
                Object memberVar = field.get(currentInstance);
                if (memberVar != null) {
                    //we found a match
                    //we need to dive into this memberVar
                    objectsFound.add(memberVar);
                    _findObjects_OfAllMemberVars_AssignableFrom(memberVar, assignableFrom, checkCollections, objectsFound, instancesChecked);
                }
            } else if (checkCollections) {
                if (Collection.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    Collection memberCollection = (Collection) field.get(currentInstance);
                    if (memberCollection != null) {
                        for (Object memberCollectionEntry : memberCollection) {
                            if (memberCollectionEntry != null) {
                                if (assignableFrom.isAssignableFrom(memberCollectionEntry.getClass())) {
                                    //we found a match
                                    //we need to dive into this memberVar, which is part of a collection
                                    objectsFound.add(memberCollectionEntry);
                                    _findObjects_OfAllMemberVars_AssignableFrom(memberCollectionEntry, assignableFrom, checkCollections, objectsFound, instancesChecked);
                                }
                            }
                        }
                    }
                }
            }
        }

    }


}