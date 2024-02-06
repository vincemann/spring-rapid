package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.syncdemo.dto.ClinicCardDto;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import com.github.vincemann.springrapid.syncdemo.service.ClinicCardService;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@Controller
public class ClinicCardController extends CrudController<ClinicCard, Long, ClinicCardService> {

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any()).thenReturn(ClinicCardDto.class);
    }

}
