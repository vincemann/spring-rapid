package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.core.controller.WebExtensionType;
import com.github.vincemann.springrapid.core.service.filter.WebExtension;
import org.springframework.core.GenericTypeResolver;

import java.util.function.Predicate;

public abstract class FilterRule<T extends WebExtension<?>>{
    private Class<T> clazz;
    private WebExtensionType type;

    public FilterRule() {
        this.clazz = (Class<T>) GenericTypeResolver.resolveTypeArgument(this.getClazz(), FilterRule.class);
        this.type = WebExtensionType.get(clazz);
    }

    public abstract void apply(T filter);


    public String getDescription(){
        return "";
    };

    public Class<?> getClazz() {
        return clazz;
    }

    public WebExtensionType getType() {
        return type;
    }

}
