package com.github.vincemann.springrapid.syncdemo.dto.abs;

import com.github.vincemann.springrapid.core.dto.IdAwareDto;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class MyIdDto<Id extends Serializable> extends IdAwareDto<Id> {

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
