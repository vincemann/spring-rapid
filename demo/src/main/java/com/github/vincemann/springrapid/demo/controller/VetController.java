package com.github.vincemann.springrapid.demo.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.demo.dtos.VetDto;
import com.github.vincemann.springrapid.demo.model.Vet;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.demo.service.VetService;

@WebController
public class VetController
        extends GenericCrudController<Vet, Long, VetService> {

    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return CrudDtoMappingContextBuilder.builder()
                .forAll(VetDto.class)
                .build();
    }
}
