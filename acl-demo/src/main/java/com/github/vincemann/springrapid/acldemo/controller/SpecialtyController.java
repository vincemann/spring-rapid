package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappings;
import org.springframework.stereotype.Controller;
import com.github.vincemann.springrapid.acldemo.dto.SpecialtyDto;
import com.github.vincemann.springrapid.acldemo.model.Specialty;
import com.github.vincemann.springrapid.acldemo.service.SpecialtyService;

@Controller
public class SpecialtyController extends CrudController<Specialty,Long, SpecialtyService> {


    @Override
    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(SpecialtyDto.class)
                .build();
    }
}
