package com.github.vincemann.springrapid.core.controller;


import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Delegating version of {@link DtoClassLocator}.
 *
 * Every controller has one. -> scope = Prototype.
 * Can have {@link this#localDelegates}, that will be called before {@link this#globalDelegate}.
 */
//@LogInteraction
public class DelegatingDtoClassLocator {
    private DtoMappingContext context;
    private List<LocalDtoClassLocator> localDelegates = new ArrayList<>();
    private DtoClassLocator globalDelegate;

    public DelegatingDtoClassLocator(DtoClassLocator globalDelegate) {
        this.globalDelegate = globalDelegate;
    }


//    //@LogInteraction
    @Cacheable(value = "findDtoClass")
    public Class<?> find(DtoRequestInfo info){
        Assert.notNull(context,"Context must be initialized");
        //local
        Optional<LocalDtoClassLocator> locator = localDelegates.stream()
                .filter(a -> a.supports(info))
                .findFirst();
        if (locator.isPresent()){
            return locator.get().find(info);
        }else {
            //global
            return globalDelegate.find(info,context);
        }
    }

    public void registerLocalLocator(LocalDtoClassLocator locator){
        Assert.notNull(context);
        locator.setContext(context);
        localDelegates.add(locator);
    }

    protected DtoMappingContext getContext() {
        return context;
    }

    protected void setContext(DtoMappingContext context) {
        this.context = context;
    }

}
