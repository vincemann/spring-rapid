package io.github.vincemann.springrapid.demo.controllers;

import io.github.vincemann.springrapid.demo.model.PetType;
import io.github.vincemann.springrapid.core.config.layers.component.WebController;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.springrapid.core.controller.springAdapter.SpringAdapterJsonDtoCrudController;

@WebController
public class PetTypeController
        extends SpringAdapterJsonDtoCrudController<PetType, Long> {

    public PetTypeController() {
        super(DtoMappingContextBuilder.builder()
                .forAll(PetType.class)
                .build()
        );
    }
}
