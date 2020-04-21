package com.naturalprogrammer.spring.lemon.authdemo.controllers;

import com.naturalprogrammer.spring.lemon.auth.controller.LemonController;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonUserDto;
import com.naturalprogrammer.spring.lemon.authdemo.dto.UserUpdateDto;
import com.naturalprogrammer.spring.lemon.authdemo.entities.User;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.CrudDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.springrapid.core.slicing.components.WebController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(LemonUserController.BASE_URI)
public class LemonUserController extends LemonController<User, Long> {

    public static final String BASE_URI = "/api/core";


    public LemonUserController() {
        super(
        		DtoMappingContextBuilder.builder()
                .forAll(LemonUserDto.class)
                .forEndpoint(CrudDtoEndpoint.PARTIAL_UPDATE, UserUpdateDto.class)
                .forEndpoint(CrudDtoEndpoint.FULL_UPDATE,UserUpdateDto.class)
                .build()
		);
    }

}