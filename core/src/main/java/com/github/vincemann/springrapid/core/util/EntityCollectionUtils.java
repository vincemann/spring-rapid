package com.github.vincemann.springrapid.core.util;

public class EntityCollectionUtils {

    public static final String ID_SUFFIX = "Id";
    public static final String IDS_SUFFIX = "Ids";


    public static String transformDtoCollectionFieldName(String dtoPropertyName) {
        if (dtoPropertyName.endsWith(ID_SUFFIX)) {
            return dtoPropertyName.substring(0, dtoPropertyName.length() - ID_SUFFIX.length());
        }
        else if (dtoPropertyName.endsWith(IDS_SUFFIX)) {
            return dtoPropertyName.substring(0, dtoPropertyName.length() - IDS_SUFFIX.length()) +"s";
        }else {
            return dtoPropertyName;
        }
    }
}
