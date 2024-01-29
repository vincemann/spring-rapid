package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappings;
import com.github.vincemann.springrapid.coredemo.dto.PetTypeDto;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.core.controller.dto.map.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.coredemo.service.PetTypeService;

@WebController
public class PetTypeController extends CrudController<PetType, Long, PetTypeService> {


    @Override
    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(PetTypeDto.class)
                .build();
    }
}
