package com.github.vincemann.springrapid.core.controller.dto.mapper;

import lombok.Getter;

import java.util.function.Predicate;

@Getter
public class Mapping {
    Predicate<DtoRequestInfo> condition;
    Class<?> dtoClass;

    public Mapping(Predicate<DtoRequestInfo> condition, Class<?> dtoClass) {
        this.condition = condition;
        this.dtoClass = dtoClass;
    }

}
