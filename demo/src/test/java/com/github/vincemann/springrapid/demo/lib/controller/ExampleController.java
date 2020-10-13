package com.github.vincemann.springrapid.demo.lib.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.CrudController;

public class ExampleController
        extends CrudController<ExampleEntity,Long,ExampleService>
{
    public ExampleController(DtoMappingContext dtoMappingContext) {
        super();
        setDtoMappingContext(dtoMappingContext);
    }

    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return null;
    }
}
