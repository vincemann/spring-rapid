package com.github.vincemann.springrapid.core.model;

import com.github.vincemann.springrapid.core.util.LazyLogUtils;

import java.io.Serializable;

public class AbstractDto<Id extends Serializable> extends IdentifiableEntityImpl<Id>{

    @Override
    public String toString() {
        return LazyLogUtils.toString(this,false);
    }
}
