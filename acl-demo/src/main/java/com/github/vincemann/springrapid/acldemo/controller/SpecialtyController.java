package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.acldemo.dto.SpecialtyDto;
import com.github.vincemann.springrapid.acldemo.model.Specialty;
import com.github.vincemann.springrapid.acldemo.service.SpecialtyService;

@WebController
public class SpecialtyController extends CrudController<Specialty,Long, SpecialtyService> {


    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(SpecialtyDto.class)
                .build();
    }
}
