package com.github.vincemann.springrapid.core.controller.dto.map;


import java.util.function.Predicate;

public class Mapping {
    Predicate<DtoRequestInfo> condition;
    Class<?> dtoClass;

    public Mapping(Predicate<DtoRequestInfo> condition, Class<?> dtoClass) {
        this.condition = condition;
        this.dtoClass = dtoClass;
    }

    public Predicate<DtoRequestInfo> getCondition() {
        return condition;
    }

    public Class<?> getDtoClass() {
        return dtoClass;
    }
}
