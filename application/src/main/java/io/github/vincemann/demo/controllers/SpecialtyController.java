package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.SpecialtyDto;
import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.generic.crud.lib.config.layers.component.ControllerComponent;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterJsonDtoCrudController;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

@ControllerComponent
public class SpecialtyController
        extends SpringAdapterJsonDtoCrudController<Specialty,Long> {

    public SpecialtyController() {
        super(DtoMappingContext.DEFAULT(SpecialtyDto.class));
    }
}
