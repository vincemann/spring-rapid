package com.github.vincemann.springrapid.core.util;

public class EntityCollectionNameUtils {

    public static final String ID_SUFFIX = "Id";
    public static final String COLLECTION_IDS_SUFFIX = "Ids";

    public static boolean isEntityCollectionIdField(String fieldName){
        return fieldName.endsWith(COLLECTION_IDS_SUFFIX);
    }

    public static boolean isEntityIdField(String fieldName){
        return fieldName.endsWith(ID_SUFFIX);
    }

    /**
     * i.E.:
     * petIds -> pets
     * specialtyIds -> specialties
     */
    public static String transformDtoEntityIdCollectionFieldName(String dtoPropertyName) {
        if (isEntityCollectionIdField(dtoPropertyName)) {
            String result = dtoPropertyName.substring(0, dtoPropertyName.length() - COLLECTION_IDS_SUFFIX.length()) +"s";
            // transform y to ie
            if (result.charAt(result.length()-2) == 'y'){
                result = result.substring(0,result.length()-2)+"ies";
            }
            return result;
        }else {
            return dtoPropertyName;
        }
    }

    public static String transformDtoEntityIdFieldName(String dtoPropertyName) {
        if (isEntityIdField(dtoPropertyName)) {
            return dtoPropertyName.substring(0, dtoPropertyName.length() - ID_SUFFIX.length());
        }
        else if (isEntityCollectionIdField(dtoPropertyName)) {
            return transformDtoEntityIdCollectionFieldName(dtoPropertyName);
        }else {
            return dtoPropertyName;
        }
    }
}
