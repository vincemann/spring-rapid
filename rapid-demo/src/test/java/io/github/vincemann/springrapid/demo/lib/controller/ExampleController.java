package io.github.vincemann.springrapid.demo.lib.controller;

import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.springAdapter.SpringAdapterJsonDtoCrudController;

public class ExampleController
        extends SpringAdapterJsonDtoCrudController<ExampleEntity,Long>
{
    public ExampleController(DtoMappingContext dtoMappingContext) {
        super(dtoMappingContext);
    }
}
