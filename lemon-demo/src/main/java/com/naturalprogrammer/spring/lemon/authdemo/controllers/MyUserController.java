package com.naturalprogrammer.spring.lemon.authdemo.controllers;

import com.naturalprogrammer.spring.lemon.auth.controller.LemonController;
import com.naturalprogrammer.spring.lemon.auth.controller.LemonDtoEndpoint;
import com.naturalprogrammer.spring.lemon.auth.controller.LemonDtoMappingContextBuilder;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.LemonFetchForeignByEmailDto;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.LemonReadUserDto;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.LemonUserDto;
import com.naturalprogrammer.spring.lemon.authdemo.domain.MySignupForm;
import com.naturalprogrammer.spring.lemon.authdemo.dto.AdminUpdateUserDto;
import com.naturalprogrammer.spring.lemon.authdemo.dto.UserUpdateDto;
import com.naturalprogrammer.spring.lemon.authdemo.domain.User;
import io.github.vincemann.springrapid.acl.Role;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MyUserController.BASE_URI)
public class MyUserController extends LemonController<User, Long> {

    public static final String BASE_URI = "/api/core";


    public MyUserController() {
        super(
                LemonDtoMappingContextBuilder.builder()
                        .forAll(LemonUserDto.class)
                        .forResponse(LemonReadUserDto.class)
                        .forEndpoint(LemonDtoEndpoint.SIGN_UP, Direction.REQUEST,MySignupForm.class)
                        .forEndpoint(RapidDtoEndpoint.UPDATE, UserUpdateDto.class)
                        .forPrincipal(DtoMappingInfo.Principal.FOREIGN)
                        .forEndpoint(LemonDtoEndpoint.FETCH_BY_EMAIL,Direction.RESPONSE, LemonFetchForeignByEmailDto.class)
                        .forAllPrincipals()
                        .withRoles(Role.ADMIN)
                        .forEndpoint(RapidDtoEndpoint.UPDATE, AdminUpdateUserDto.class)
                        .build()
        );
    }


}