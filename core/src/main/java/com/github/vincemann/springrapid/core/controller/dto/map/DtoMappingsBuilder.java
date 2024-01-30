package com.github.vincemann.springrapid.core.controller.dto.map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.function.Predicate;

@Slf4j
public class DtoMappingsBuilder {

    private Predicate<DtoRequestInfo> predicate;
    private DtoMappings mappings;

    public DtoMappingsBuilder() {
        this.mappings = new DtoMappings();
    }

    public DtoMappingsBuilder when(Predicate<DtoRequestInfo> condition) {
        Assert.isNull(predicate,"after one call of when must come one call of thenReturn");
        this.predicate = condition;
        return this;
    }

    public void thenReturn(Class<?> dtoClass){
        Assert.notNull(predicate,"need to configure at lease one condition before calling then return");
        mappings.get().add(new Mapping(predicate,dtoClass));
        this.predicate = null;
    }

    public DtoMappings build(){
        return mappings;
    }


}
