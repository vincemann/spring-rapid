package com.github.vincemann.springrapid.authdemo.controllers;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.controller.UserDtoMappingContextBuilder;
import com.github.vincemann.springrapid.authdemo.domain.MySignupForm;
import com.github.vincemann.springrapid.authdemo.domain.User;
import com.github.vincemann.springrapid.authdemo.dto.AdminUpdatesUserDto;
import com.github.vincemann.springrapid.authdemo.dto.UserUpdateDto;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.security.Roles;
import org.springframework.stereotype.Controller;

@Controller
public class MyUserController extends AbstractUserController<User, Long, MyUserService>  {


    @Override
    protected DtoMappingContext provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
        return builder
                .forEndpoint(getUpdateUrl(), UserUpdateDto.class)
                .forEndpoint(getAuthProperties().getController().getSignupUrl(), Direction.REQUEST, MySignupForm.class)

                .withRoles(Roles.ADMIN)
                .forEndpoint(getUpdateUrl(), AdminUpdatesUserDto.class)
                .build();
    }

}