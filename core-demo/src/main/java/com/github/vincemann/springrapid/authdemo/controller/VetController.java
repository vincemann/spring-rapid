package com.github.vincemann.springrapid.authdemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.authdemo.dtos.VetDto;
import com.github.vincemann.springrapid.authdemo.model.Vet;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.authdemo.service.VetService;

@WebController
public class VetController
        extends CrudController<Vet, Long, VetService> {

    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(VetDto.class).build();
    }
}
