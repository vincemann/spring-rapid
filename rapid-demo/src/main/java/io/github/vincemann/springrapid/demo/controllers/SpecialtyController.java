package io.github.vincemann.springrapid.demo.controllers;

import io.github.vincemann.springrapid.demo.dtos.SpecialtyDto;
import io.github.vincemann.springrapid.demo.model.Specialty;
import io.github.vincemann.springrapid.core.slicing.components.WebController;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;
import io.github.vincemann.springrapid.demo.service.SpecialtyService;

@WebController
public class SpecialtyController extends RapidController<Specialty,Long, SpecialtyService> {

    public SpecialtyController() {
        super(DtoMappingContextBuilder.builder()
                .forAll(SpecialtyDto.class)
                .build());
    }
}
