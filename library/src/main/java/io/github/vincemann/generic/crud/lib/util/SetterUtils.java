package io.github.vincemann.generic.crud.lib.util;

public class SetterUtils {

    public static <T> T returnIfNotNull(T oldValue, T newValue){
        if(newValue!=null){
            return newValue;
        }else {
            return oldValue;
        }
    }
}
