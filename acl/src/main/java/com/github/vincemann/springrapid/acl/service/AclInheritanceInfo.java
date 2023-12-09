package com.github.vincemann.springrapid.acl.service;

import com.github.hervian.reflection.Types;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class AclInheritanceInfo {
    private Method source;
    private EntityFilter sourceFilter;
    private Method target;
    private EntityFilter targetFilter;
    private AceFilter aceFilter;

    protected boolean matches(Class<?> entityClass){
        return this.getSource().getGenericReturnType().equals(entityClass);
    }

    @Builder
    public AclInheritanceInfo(Types.Supplier<?> source, EntityFilter sourceFilter, Types.Supplier<?> target, EntityFilter targetFilter, AceFilter aceFilter) {
        this.source = Types.createMethod(source);
        this.sourceFilter = sourceFilter;
        this.target = Types.createMethod(target);
        this.targetFilter = targetFilter;
        this.aceFilter = aceFilter;
    }


}
