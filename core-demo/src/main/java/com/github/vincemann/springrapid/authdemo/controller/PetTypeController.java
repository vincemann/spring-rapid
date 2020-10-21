package com.github.vincemann.springrapid.authdemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.authdemo.model.PetType;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.authdemo.service.PetTypeService;

@WebController
public class PetTypeController extends CrudController<PetType, Long, PetTypeService> {


    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(PetType.class)
                .build();
    }
}
