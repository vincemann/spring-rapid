package io.github.vincemann.springrapid.demo.lib.controller;

import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;

public class ExampleRapidController
        extends RapidController<ExampleEntity,Long>
{
    public ExampleRapidController(DtoMappingContext dtoMappingContext) {
        super(dtoMappingContext);
    }
}
