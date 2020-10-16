package com.github.vincemann.springlemon.demo.controllers;

import com.github.vincemann.springlemon.auth.controller.AbstractUserController;
import com.github.vincemann.springlemon.auth.controller.UserDtoMappingContextBuilder;
import com.github.vincemann.springlemon.demo.domain.MySignupForm;
import com.github.vincemann.springlemon.demo.domain.User;
import com.github.vincemann.springlemon.demo.dto.AdminUpdatesUserDto;
import com.github.vincemann.springlemon.demo.dto.UserUpdateDto;
import com.github.vincemann.springlemon.demo.services.MyUserService;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.security.RapidRoles;
import org.springframework.stereotype.Controller;

@Controller
public class UserController extends AbstractUserController<User, Long, MyUserService>  {


    @Override
    protected DtoMappingContext provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
        return builder
                .forEndpoint(getCoreProperties().controller.endpoints.update, UserUpdateDto.class)
                .forEndpoint(getLemonProperties().getController().getSignupUrl(), Direction.REQUEST,MySignupForm.class)
                .withRoles(RapidRoles.ADMIN)
                .forEndpoint(getCoreProperties().controller.endpoints.update, AdminUpdatesUserDto.class)
                .build();
    }

}