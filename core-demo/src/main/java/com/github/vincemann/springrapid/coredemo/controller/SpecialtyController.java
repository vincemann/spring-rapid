package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.coredemo.service.SpecialtyService;
import org.springframework.stereotype.Controller;
import com.github.vincemann.springrapid.coredemo.dto.SpecialtyDto;
import com.github.vincemann.springrapid.coredemo.model.Specialty;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@Controller
public class SpecialtyController extends CrudController<Specialty,Long, SpecialtyService> {

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any())
                .thenReturn(SpecialtyDto.class);
    }

}
