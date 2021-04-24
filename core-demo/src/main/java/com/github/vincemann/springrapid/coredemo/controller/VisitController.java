package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.coredemo.dtos.VisitDto;
import com.github.vincemann.springrapid.coredemo.model.Visit;
import com.github.vincemann.springrapid.coredemo.service.VisitService;

public class VisitController
        extends CrudController<Visit, Long, VisitService>
{
    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(VisitDto.class).build();
    }
}
