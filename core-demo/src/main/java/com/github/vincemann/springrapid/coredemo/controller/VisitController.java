package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.coredemo.dto.VisitDto;
import com.github.vincemann.springrapid.coredemo.model.Visit;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@WebController
public class VisitController
        extends CrudController<Visit, Long>
{

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any())
                .thenReturn(VisitDto.class);
    }
}
