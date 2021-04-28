package com.github.vincemann.springrapid.authdemo.controllers;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.controller.UserDtoMappingContextBuilder;
import com.github.vincemann.springrapid.authdemo.model.MySignupDto;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.dto.AdminUpdatesUserDto;
import com.github.vincemann.springrapid.authdemo.dto.UserUpdateDto;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.security.Roles;
import org.springframework.stereotype.Controller;

@Controller
public class UserController extends AbstractUserController<User, Long, MyUserService>  {


    @Override
    protected DtoMappingContext provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
        return builder
                .forEndpoint(getUpdateUrl(), UserUpdateDto.class)
                .forEndpoint(getAuthProperties().getController().getSignupUrl(), Direction.REQUEST, MySignupDto.class)

                .withRoles(Roles.ADMIN)
                .forEndpoint(getUpdateUrl(), AdminUpdatesUserDto.class)
                .build();
    }

}