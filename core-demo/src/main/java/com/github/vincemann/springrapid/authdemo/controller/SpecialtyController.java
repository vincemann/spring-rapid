package com.github.vincemann.springrapid.authdemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.authdemo.dtos.SpecialtyDto;
import com.github.vincemann.springrapid.authdemo.model.Specialty;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.authdemo.service.SpecialtyService;

@WebController
public class SpecialtyController extends CrudController<Specialty,Long, SpecialtyService> {


    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(SpecialtyDto.class)
                .build();
    }
}
