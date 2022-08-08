package com.github.vincemann.springrapid.core.controller;

import lombok.Getter;
import lombok.Setter;

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

    public static CrudEndpointInfo exposeNone(){
        CrudEndpointInfo info = new CrudEndpointInfo();
        info.setExposeCreate(false);
        info.setExposeFind(false);
        info.setExposeUpdate(false);
        info.setExposeDelete(false);
        info.setExposeFindAll(false);
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
}
