package io.github.vincemann.springrapid.commons;

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
