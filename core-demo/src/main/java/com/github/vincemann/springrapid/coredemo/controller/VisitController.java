package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoMappings;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.coredemo.dto.VisitDto;
import com.github.vincemann.springrapid.coredemo.model.Visit;
import com.github.vincemann.springrapid.coredemo.service.VisitService;

@WebController
public class VisitController
        extends CrudController<Visit, Long, VisitService>
{
    @Override
    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(VisitDto.class).build();
    }
}
