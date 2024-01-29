package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import org.springframework.stereotype.Controller;
import com.github.vincemann.springrapid.coredemo.dto.ClinicCardDto;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@Controller
public class ClinicCardController extends CrudController<ClinicCard, Long> {

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any()).thenReturn(ClinicCardDto.class);
    }

}
