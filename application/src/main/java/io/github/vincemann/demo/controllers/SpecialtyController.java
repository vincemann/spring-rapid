package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.repositories.SpecialtyRepository;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.MappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import io.github.vincemann.demo.dtos.SpecialtyDto;
import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.service.SpecialtyService;
import org.springframework.stereotype.Controller;

@Controller
public class SpecialtyController
        extends DtoCrudController_SpringAdapter<Specialty,Long, SpecialtyRepository> {

    public SpecialtyController() {
        super(MappingContext.DEFAULT(SpecialtyDto.class));
    }
}
