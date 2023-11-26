package com.github.vincemann.springrapid.acldemo.model.abs;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class MyIdentifiableEntity<Id extends Serializable> extends IdentifiableEntityImpl<Id> {

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
