package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappings;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.coredemo.dto.PetTypeDto;
import com.github.vincemann.springrapid.coredemo.dto.VetDto;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.core.controller.dto.map.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.coredemo.service.VetService;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@WebController
public class VetController extends CrudController<Vet, Long> {


    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any())
                .thenReturn(VetDto.class);
    }

}
