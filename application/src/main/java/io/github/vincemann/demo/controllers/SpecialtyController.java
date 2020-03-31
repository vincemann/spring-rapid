package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.SpecialtyDto;
import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.generic.crud.lib.config.layers.component.WebController;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterJsonDtoCrudController;

@WebController
public class SpecialtyController
        extends SpringAdapterJsonDtoCrudController<Specialty,Long> {

    public SpecialtyController() {
        super(DtoMappingContextBuilder.builder()
                .forAll(SpecialtyDto.class)
                .build());
    }
}
