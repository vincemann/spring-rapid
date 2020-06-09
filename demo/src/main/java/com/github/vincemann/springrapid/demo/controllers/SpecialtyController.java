package com.github.vincemann.springrapid.demo.controllers;

import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.demo.dtos.SpecialtyDto;
import com.github.vincemann.springrapid.demo.model.Specialty;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.rapid.RapidController;
import com.github.vincemann.springrapid.demo.service.SpecialtyService;

@WebController
public class SpecialtyController extends RapidController<Specialty,Long, SpecialtyService> {

    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return DtoMappingContextBuilder.builder()
                .forAll(SpecialtyDto.class)
                .build();
    }
}
