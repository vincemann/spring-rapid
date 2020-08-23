package com.github.vincemann.springlemon.demo.controllers;

import com.github.vincemann.springlemon.auth.controller.LemonController;
import com.github.vincemann.springlemon.auth.controller.LemonDtoEndpoint;
import com.github.vincemann.springlemon.auth.controller.LemonDtoMappingContextBuilder;
import com.github.vincemann.springlemon.demo.domain.User;
import com.github.vincemann.springlemon.demo.domain.MySignupForm;
import com.github.vincemann.springlemon.demo.dto.AdminUpdateUserDto;
import com.github.vincemann.springlemon.demo.dto.UserUpdateDto;
import com.github.vincemann.springrapid.core.security.RapidRole;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MyUserController.BASE_URI)
public class MyUserController extends LemonController<User, Long>  {

    public static final String BASE_URI = "/api/core";


    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return LemonDtoMappingContextBuilder.builder((DtoMappingContext) super.provideDtoMappingContext())
                .forEndpoint(RapidDtoEndpoint.UPDATE, UserUpdateDto.class)
                .forEndpoint(LemonDtoEndpoint.SIGN_UP, Direction.REQUEST,MySignupForm.class)
                .withRoles(RapidRole.ADMIN)
                .forEndpoint(RapidDtoEndpoint.UPDATE, AdminUpdateUserDto.class)
                .build();
    }
}