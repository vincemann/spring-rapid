package com.github.vincemann.springrapid.demo.lib.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;

public class ExampleController
        extends CrudController<ExampleEntity,Long,ExampleService>
{
    public ExampleController(DtoMappingContext dtoMappingContext) {
        super();
        setDtoMappingContext(dtoMappingContext);
    }

    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return null;
    }

}
