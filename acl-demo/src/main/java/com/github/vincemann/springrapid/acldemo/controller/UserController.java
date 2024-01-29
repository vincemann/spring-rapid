package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.dto.user.FullUserDto;
import com.github.vincemann.springrapid.acldemo.dto.user.UUIDSignupResponseDto;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.controller.UserDtoMappingContextBuilder;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.core.controller.dto.mapper.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoMappings;
import com.github.vincemann.springrapid.core.security.Roles;
import org.springframework.stereotype.Controller;

@Controller
public class UserController extends AbstractUserController<User, Long, MyUserService>  {

    @Override
    protected DtoMappings provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
        return builder
                .forEndpoint(getSignupUrl(), Direction.REQUEST, SignupDto.class)
                .forEndpoint(getSignupUrl(), Direction.RESPONSE, UUIDSignupResponseDto.class)

                .withRoles(Roles.ADMIN)
                .forAll(FullUserDto.class)
                
                .build();
    }
}