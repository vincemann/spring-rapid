package com.naturalprogrammer.spring.lemon.authdemo.controllers;

import com.naturalprogrammer.spring.lemon.authdemo.dto.UserUpdateDto;
import com.naturalprogrammer.spring.lemon.authdemo.entities.User;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.springrapid.core.controller.rapid.EndpointsExposureContext;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;

public class UserController extends RapidController<User, Long> {
    public UserController() {
        super(
                DtoMappingContextBuilder.builder()
                .forAll(UserUpdateDto.class)
                .build()
        );
    }

    @Override
    public void injectEndpointsExposureContext(EndpointsExposureContext endpointsExposureContext) {
        endpointsExposureContext.setFindAllEndpointExposed(false);
        endpointsExposureContext.setFindEndpointExposed(false);
        super.injectEndpointsExposureContext(endpointsExposureContext);
    }
}
