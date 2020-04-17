package com.naturalprogrammer.spring.lemon.authdemo.controllers;

import com.naturalprogrammer.spring.lemon.auth.controller.LemonController;
import com.naturalprogrammer.spring.lemon.auth.security.domain.UserDto;
import com.naturalprogrammer.spring.lemon.authdemo.entities.User;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.springrapid.core.slicing.components.WebController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@WebController
@RequestMapping(MyController.BASE_URI)
public class MyController extends LemonController<User, Long> {

    //todo get from config file
    public static final String BASE_URI = "/api/core";


    public MyController() {
        super(
        		DtoMappingContextBuilder.builder()
                .forAll(UserDto.class).build()
		);
    }

}