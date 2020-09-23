package com.github.vincemann.springrapid.demo.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.demo.model.PetType;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.RapidController;
import com.github.vincemann.springrapid.demo.service.PetTypeService;

@WebController
public class PetTypeController extends RapidController<PetType, Long, PetTypeService> {

    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return DtoMappingContextBuilder.builder()
                .forAll(PetType.class)
                .build();
    }
}
