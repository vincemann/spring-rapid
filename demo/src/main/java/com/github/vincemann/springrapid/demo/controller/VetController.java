package com.github.vincemann.springrapid.demo.controller;

import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.demo.dtos.VetDto;
import com.github.vincemann.springrapid.demo.model.Vet;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.rapid.RapidController;
import com.github.vincemann.springrapid.demo.service.VetService;

@WebController
public class VetController
        extends RapidController<Vet, Long, VetService> {

    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return DtoMappingContextBuilder.builder()
                .forAll(VetDto.class)
                .build();
    }
}
