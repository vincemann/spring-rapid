package com.github.vincemann.springrapid.core.dto;




//import com.github.vincemann.smartlogger.SmartLogger;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import java.io.Serializable;

public class IdAwareDto<Id extends Serializable> extends IdentifiableEntityImpl<Id> {

//    @Override
//    public String toString() {
//        return SmartLogger.builder()
//                .build()
//                .toString(this);
//    }
}
