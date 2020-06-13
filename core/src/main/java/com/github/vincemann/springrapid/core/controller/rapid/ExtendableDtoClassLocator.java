package com.github.vincemann.springrapid.core.controller.rapid;

import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Every controller has one. -> scope = Prototype.
 * Can have {@link this#localLocators}, that will be called before {@link this#globalLocator}.
 */
public class ExtendableDtoClassLocator {
    private DtoMappingContext context;
    private List<LocalDtoClassLocator> localLocators = new ArrayList<>();

    public ExtendableDtoClassLocator(DtoClassLocator globalLocator) {
        this.globalLocator = globalLocator;
    }

    private DtoClassLocator globalLocator;

    public Class<?> find(DtoMappingInfo info){
        Assert.notNull(context,"Context must be initialized");
        //local
        Optional<LocalDtoClassLocator> addOn = localLocators.stream()
                .filter(a -> a.supports(info))
                .findFirst();
        if (addOn.isPresent()){
            return addOn.get().find(info);
        }else {
            //global
            return globalLocator.find(info,context);
        }
    }

    public void registerLocalLocator(LocalDtoClassLocator locator){
        Assert.notNull(context);
        locator.setContext(context);
        localLocators.add(locator);
    }

    protected DtoMappingContext getContext() {
        return context;
    }

    protected void setContext(DtoMappingContext context) {
        this.context = context;
    }

}
