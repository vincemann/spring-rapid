package io.github.vincemann.generic.crud.lib.util;

import java.util.Collection;

public class CollectionUtils {

    /**
     * checks whether object o is in collection
     * does not utilize the equals method, instead checks equality by reference
     * @param collection
     * @return
     */
    public static boolean containsByReference(Collection collection, Object o){
        for(Object object: collection){
            if(o == object){
                return true;
            }
        }
        return false;
    }

    public static boolean removeByReference(Collection collection, Object o){
        int indexToRemove = -1;
        int counter = 0;
        for(Object object:collection){
            if(object==o){
                indexToRemove=counter;
                break;
            }else {
                counter++;
            }
        }
        if(indexToRemove==-1){
            return false;
        }else {
            collection.remove(indexToRemove);
            return true;
        }
    }
}
