package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.coredemo.dtos.VetDto;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.coredemo.service.VetService;

@WebController
public class VetController
        extends CrudController<Vet, Long, VetService> {

    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(VetDto.class).build();
    }
}
