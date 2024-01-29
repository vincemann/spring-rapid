package com.github.vincemann.springrapid.authdemo.controller;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.controller.UserDtoMappingContextBuilder;
import com.github.vincemann.springrapid.authdemo.dto.*;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.service.JpaUserService;
import com.github.vincemann.springrapid.core.controller.dto.mapper.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoMappings;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoRequestInfo;
import com.github.vincemann.springrapid.core.security.Roles;
import org.springframework.stereotype.Controller;

@Controller
public class UserController extends AbstractUserController<User, Long, JpaUserService>  {

    @Override
    protected DtoMappings provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
        return builder
                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(getUpdateUrl(), UserUpdatesOwnDto.class)

                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(getRequestContactInformationChangeUrl(),Direction.REQUEST, RequestEmailChangeDto.class)

                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(getSignupUrl(), Direction.REQUEST, MySignupDto.class)
                .forEndpoint(getSignupUrl(), Direction.RESPONSE, MyFindOwnUserDto.class)

                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(getVerifyUserUrl(),Direction.RESPONSE, MyFindOwnUserDto.class)

                .withAllRoles()
                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .forResponse(MyFindOwnUserDto.class)

                .withAllPrincipals()
                .withRoles(Roles.ADMIN)
                .forEndpoint(getUpdateUrl(), MyFullUserDto.class)
                .build();
    }


}