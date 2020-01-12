package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.repositories.PetTypeRepository;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import org.springframework.stereotype.Controller;

@Controller
public class PetTypeController
        extends DtoCrudController_SpringAdapter<PetType, Long, PetTypeRepository> {

    public PetTypeController() {
        super(DtoMappingContext.DEFAULT(PetType.class));
    }
}
