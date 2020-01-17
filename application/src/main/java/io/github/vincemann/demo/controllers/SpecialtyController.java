package io.github.vincemann.demo.controllers;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import io.github.vincemann.demo.dtos.SpecialtyDto;
import io.github.vincemann.demo.model.Specialty;
import org.springframework.stereotype.Controller;

@Controller
public class SpecialtyController
        extends SpringAdapterDtoCrudController<Specialty,Long> {

    public SpecialtyController() {
        super(DtoMappingContext.DEFAULT(SpecialtyDto.class));
    }
}
