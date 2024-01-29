package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoMappings;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.syncdemo.dto.VetDto;
import com.github.vincemann.springrapid.syncdemo.model.Vet;
import com.github.vincemann.springrapid.syncdemo.service.VetService;

@WebController
public class VetController
        extends CrudController<Vet, Long, VetService> {

    @Override
    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(VetDto.class).build();
    }
}
