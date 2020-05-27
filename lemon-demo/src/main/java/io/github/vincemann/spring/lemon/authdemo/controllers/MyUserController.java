package io.github.vincemann.spring.lemon.authdemo.controllers;

import io.github.spring.lemon.auth.controller.LemonController;
import io.github.spring.lemon.auth.controller.LemonDtoEndpoint;
import io.github.spring.lemon.auth.controller.LemonDtoMappingContextBuilder;
import io.github.vincemann.spring.lemon.authdemo.domain.MySignupForm;
import io.github.vincemann.spring.lemon.authdemo.dto.AdminUpdateUserDto;
import io.github.vincemann.spring.lemon.authdemo.dto.UserUpdateDto;
import io.github.vincemann.spring.lemon.authdemo.domain.User;
import io.github.vincemann.springrapid.acl.Role;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoMappingContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MyUserController.BASE_URI)
public class MyUserController extends LemonController<User, Long> {

    public static final String BASE_URI = "/api/core";


    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return LemonDtoMappingContextBuilder.builder((RapidDtoMappingContext) super.provideDtoMappingContext())
                .forEndpoint(RapidDtoEndpoint.UPDATE, UserUpdateDto.class)
                .forEndpoint(LemonDtoEndpoint.SIGN_UP, Direction.REQUEST,MySignupForm.class)
                .withRoles(Role.ADMIN)
                .forEndpoint(RapidDtoEndpoint.UPDATE, AdminUpdateUserDto.class)
                .build();
    }
}