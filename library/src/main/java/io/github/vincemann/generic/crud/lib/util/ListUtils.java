package io.github.vincemann.generic.crud.lib.util;

import java.util.List;

public class ListUtils {

    /**
     * checks whether object o is in list
     * does not utilize the equals method, instead checks equality by reference
     * @param list
     * @return
     */
    public static boolean containsByReference(List list, Object o){
        for(Object object: list){
            if(o == object){
                return true;
            }
        }
        return false;
    }
}
