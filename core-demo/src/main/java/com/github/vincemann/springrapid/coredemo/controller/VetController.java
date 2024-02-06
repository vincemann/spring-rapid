package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.coredemo.dto.VetDto;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.coredemo.service.VetService;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@Controller
public class VetController extends CrudController<Vet, Long, VetService> {


    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any())
                .thenReturn(VetDto.class);
    }

}
