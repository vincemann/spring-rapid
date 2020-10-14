package com.github.vincemann.springrapid.demo.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.demo.dtos.SpecialtyDto;
import com.github.vincemann.springrapid.demo.model.Specialty;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.demo.service.SpecialtyService;

@WebController
public class SpecialtyController extends GenericCrudController<Specialty,Long, SpecialtyService> {

    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return DtoMappingContextBuilder.builder()
                .forAll(SpecialtyDto.class)
                .build();
    }
}
