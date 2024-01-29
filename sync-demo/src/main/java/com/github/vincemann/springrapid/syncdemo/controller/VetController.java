package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.syncdemo.dto.VetDto;
import com.github.vincemann.springrapid.syncdemo.model.Vet;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@Controller
public class VetController
        extends CrudController<Vet, Long> {


    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any()).thenReturn(VetDto.class);
    }
}
