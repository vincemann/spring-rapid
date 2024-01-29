package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.coredemo.dto.ClinicCardDto;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@WebController
public class ClinicCardController extends CrudController<ClinicCard, Long> {

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any()).thenReturn(ClinicCardDto.class);
    }

}
