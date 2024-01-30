package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.dto.PetTypeDto;
import com.github.vincemann.springrapid.acldemo.model.PetType;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@Controller
public class PetTypeController extends SecuredCrudController<PetType, Long> {

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any())
                .thenReturn(PetTypeDto.class);
    }

}
