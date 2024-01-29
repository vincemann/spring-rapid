package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappings;
import org.springframework.stereotype.Controller;
import com.github.vincemann.springrapid.acldemo.model.PetType;
import com.github.vincemann.springrapid.acldemo.service.PetTypeService;

@Controller
public class PetTypeController extends SecuredCrudController<PetType, Long, PetTypeService> {


    @Override
    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(PetType.class)
                .build();
    }
}
