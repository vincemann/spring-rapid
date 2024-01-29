package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import org.springframework.stereotype.Controller;
import com.github.vincemann.springrapid.coredemo.dto.VisitDto;
import com.github.vincemann.springrapid.coredemo.model.Visit;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@Controller
public class VisitController
        extends CrudController<Visit, Long>
{

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any())
                .thenReturn(VisitDto.class);
    }
}
