package com.github.vincemann.springrapid.acl.service;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntityFilter<T> {

    private Predicate<T> matchFunction;

    public EntityFilter(Predicate<T> matchFunction) {
        this.matchFunction = matchFunction;
    }

    public boolean matches(T entity){
        return matchFunction.test(entity);
    }
    public Collection<T> apply(Collection<T> entities) {
        return entities.stream().filter(matchFunction).collect(Collectors.toList());
    }
}
