package com.github.vincemann.springrapid.authdemo.controllers;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.controller.UserDtoMappingContextBuilder;
import com.github.vincemann.springrapid.auth.domain.dto.user.RapidFindOwnUserDto;
import com.github.vincemann.springrapid.authdemo.dto.MyFindOwnUserDto;
import com.github.vincemann.springrapid.authdemo.model.MySignupDto;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.dto.MyFullUserDto;
import com.github.vincemann.springrapid.authdemo.dto.MyUserUpdatesOwnDto;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;
import com.github.vincemann.springrapid.core.security.Roles;
import org.springframework.stereotype.Controller;

@Controller
public class UserController extends AbstractUserController<User, Long, MyUserService>  {


    @Override
    protected DtoMappingContext provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
        return builder
                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(getUpdateUrl(), MyUserUpdatesOwnDto.class)

                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(getAuthProperties().getController().getSignupUrl(), Direction.REQUEST, MySignupDto.class)
                .forEndpoint(getAuthProperties().getController().getSignupUrl(), Direction.RESPONSE, MyFindOwnUserDto.class)

                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(getAuthProperties().getController().getVerifyUserUrl(),Direction.RESPONSE, MyFindOwnUserDto.class)

                .withAllRoles()
                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .forResponse(MyFindOwnUserDto.class)

                .withAllPrincipals()
                .withRoles(Roles.ADMIN)
                .forEndpoint(getUpdateUrl(), MyFullUserDto.class)
                .build();
    }

}