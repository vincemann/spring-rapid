package com.github.vincemann.springrapid.core.controller;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Set;

@Getter
@Setter
/**
 * Gives User fine grained control about which endpoints should be exposed by {@link GenericCrudController}.
 */
public class CrudEndpointInfo {
    private boolean exposeCreate =true;
    private boolean exposeFind =true;
    private boolean exposeUpdate =true;
    private boolean exposeDelete =true;
    private boolean exposeFindAll =true;
    private boolean exposeFindSome =true;

    public static CrudEndpointInfo exposeNone(){
        CrudEndpointInfo info = new CrudEndpointInfo();
        info.setExposeCreate(false);
        info.setExposeFind(false);
        info.setExposeUpdate(false);
        info.setExposeDelete(false);
        info.setExposeFindAll(false);
        info.setExposeFindSome(false);
        return info;
    }

//    @Builder
//    public CrudEndpointInfo(Boolean exposeCreate, Boolean exposeFind, Boolean exposeUpdate, Boolean exposeDelete, Boolean exposeFindAll) {
//        if(exposeCreate ==null){
//            this.exposeCreate =true;
//        }else {
//            this.exposeCreate = exposeCreate;
//        }
//
//        if(exposeFind ==null){
//            this.exposeFind =true;
//        }else {
//            this.exposeFind = exposeFind;
//        }
//
//        if(exposeDelete ==null){
//            this.exposeDelete =true;
//        }else {
//            this.exposeDelete = exposeDelete;
//        }
//
//
//        if(exposeUpdate ==null){
//            this.exposeUpdate =true;
//        }else {
//            this.exposeUpdate = exposeUpdate;
//        }
//
//        if(exposeFindAll ==null){
//            this.exposeFindAll =true;
//        }else {
//            this.exposeFindAll = exposeFindAll;
//        }
//    }

    public CrudEndpointInfo() {
    }

    @Override
    public String toString() {
        return "CrudEndpointInfo{" +
                "exposeCreate=" + exposeCreate +
                ", exposeFind=" + exposeFind +
                ", exposeUpdate=" + exposeUpdate +
                ", exposeDelete=" + exposeDelete +
                ", exposeFindAll=" + exposeFindAll +
                ", exposeFindSome=" + exposeFindSome +
                '}';
    }
}
