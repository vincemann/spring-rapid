package io.github.vincemann.generic.crud.lib.util;

import java.util.List;

public class ListUtils {

    public static boolean containsByReference(List list, Object object){
        for (Object o : list) {
            if(o == object){
                return true;
            }
        }
        return false;
    }
}
