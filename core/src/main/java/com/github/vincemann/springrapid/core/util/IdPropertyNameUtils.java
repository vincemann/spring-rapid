package com.github.vincemann.springrapid.core.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class IdPropertyNameUtils {

    public static final String ID_SUFFIX = "Id";
    public static final String COLLECTION_IDS_SUFFIX = "Ids";

    public static boolean isCollectionIdField(String fieldName){
        return fieldName.endsWith(COLLECTION_IDS_SUFFIX);
    }

    public static boolean isIdField(String fieldName){
        return fieldName.endsWith(ID_SUFFIX);
    }

    /**
     * i.E.:
     * petIds -> pets
     * specialtyIds -> specialtys
     */
    public static String transformIdCollectionFieldName(String propertyName) {
        if (isCollectionIdField(propertyName)) {
            String result = propertyName.substring(0, propertyName.length() - COLLECTION_IDS_SUFFIX.length()) +"s";
            // dont do this, i.E. toys plural is toys not toies, people have to just add an s for plural
//            // transform y to ie
//            if (result.charAt(result.length()-2) == 'y'){
//                result = result.substring(0,result.length()-2)+"ies";
//            }
            return result;
        }else {
            return propertyName;
        }
    }

    public static String[] transformIdFieldNames(String... propertyNames) {
        return Arrays.stream(propertyNames).map(IdPropertyNameUtils::transformIdFieldName).toArray(String[]::new);
    }

    public static String[] transformIdFieldNames(Set<String> propertyNames) {
        return propertyNames.stream().map(IdPropertyNameUtils::transformIdFieldName).toArray(String[]::new);
    }

    //            Set<String> propertiesToMap =


    public static String transformIdFieldName(String propertyName) {
        if (isIdField(propertyName)) {
            return propertyName.substring(0, propertyName.length() - ID_SUFFIX.length());
        }
        else if (isCollectionIdField(propertyName)) {
            return transformIdCollectionFieldName(propertyName);
        }else {
            return propertyName;
        }
    }
}
