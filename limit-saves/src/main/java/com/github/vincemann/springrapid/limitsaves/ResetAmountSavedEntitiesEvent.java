package com.github.vincemann.springrapid.limitsaves;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Arrays;
import java.util.List;

@Getter
public class ResetAmountSavedEntitiesEvent extends ApplicationEvent {

    private List<Class<?>> targetEntityClasses;

    public ResetAmountSavedEntitiesEvent(Object source, Class<?>... targetEntityClasses) {
        super(source);
        this.targetEntityClasses = Lists.newArrayList(targetEntityClasses);
    }


}
