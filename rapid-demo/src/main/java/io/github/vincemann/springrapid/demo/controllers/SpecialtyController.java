package io.github.vincemann.springrapid.demo.controllers;

import io.github.vincemann.springrapid.demo.dtos.SpecialtyDto;
import io.github.vincemann.springrapid.demo.model.Specialty;
import io.github.vincemann.springrapid.core.config.layers.component.WebController;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.springrapid.core.controller.springAdapter.SpringAdapterJsonDtoCrudController;

@WebController
public class SpecialtyController
        extends SpringAdapterJsonDtoCrudController<Specialty,Long> {

    public SpecialtyController() {
        super(DtoMappingContextBuilder.builder()
                .forAll(SpecialtyDto.class)
                .build());
    }
}
