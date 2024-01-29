package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.coredemo.dto.PetTypeDto;
import com.github.vincemann.springrapid.coredemo.model.PetType;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@WebController
public class PetTypeController extends CrudController<PetType, Long> {


    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any())
                .thenReturn(PetTypeDto.class);
    }
}
