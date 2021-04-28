package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.dto.user.AdminUpdatesUserDto;
import com.github.vincemann.springrapid.acldemo.dto.user.MySignupDto;
import com.github.vincemann.springrapid.acldemo.dto.user.SignupResponseDto;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.service.jpa.MyUserServiceImpl;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.controller.UserDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.security.Roles;
import org.springframework.stereotype.Controller;

@Controller
public class UserController extends AbstractUserController<User, Long, MyUserServiceImpl>  {


    @Override
    protected DtoMappingContext provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
        return builder
                .forEndpoint(getAuthProperties().getController().getSignupUrl(), Direction.REQUEST, MySignupDto.class)
                .forEndpoint(getAuthProperties().getController().getSignupUrl(), Direction.RESPONSE, SignupResponseDto.class)

                .withRoles(Roles.ADMIN)
                .forEndpoint(getUpdateUrl(), AdminUpdatesUserDto.class)
                .build();
    }

}