package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappings;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.acldemo.model.PetType;
import com.github.vincemann.springrapid.acldemo.service.PetTypeService;

@WebController
public class PetTypeController extends SecuredCrudController<PetType, Long, PetTypeService> {


    @Override
    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(PetType.class)
                .build();
    }
}
