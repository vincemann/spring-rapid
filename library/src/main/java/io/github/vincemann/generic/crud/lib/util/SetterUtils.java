package io.github.vincemann.generic.crud.lib.util;

public class SetterUtils {

    public static <T> void setIfNotNull(T ptr, T newValue){
        if(newValue!=null){
            ptr=newValue;
        }
        //else dont set
    }
}
