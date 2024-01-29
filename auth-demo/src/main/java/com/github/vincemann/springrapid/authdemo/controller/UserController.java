package com.github.vincemann.springrapid.authdemo.controller;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.authdemo.dto.*;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class UserController extends AbstractUserController<User, Long>  {

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {

        builder.when(endpoint(getUpdateUrl()).and(roles(AuthRoles.ADMIN)))
                        .thenReturn(MyFullUserDto.class);

        builder.when(endpoint(getUpdateUrl()))
                .thenReturn(UserUpdatesOwnDto.class);

        builder.when(endpoint(getRequestContactInformationChangeUrl()).and(direction(Direction.REQUEST)))
                .thenReturn(RequestEmailChangeDto.class);

        builder.when(endpoint(getSignupUrl()).and(direction(Direction.REQUEST)))
                .thenReturn(MySignupDto.class);

        builder.when(endpoint(getSignupUrl()).and(direction(Direction.RESPONSE)))
                .thenReturn(MyFindOwnUserDto.class);

        builder.when(endpoint(getVerifyUserUrl()).and(direction(Direction.RESPONSE)))
                .thenReturn(MyFindOwnUserDto.class);

        builder.when(direction(Direction.RESPONSE).and(principal(Principal.OWN)))
                .thenReturn(MyFindOwnUserDto.class);


        super.configureDtoMappings(builder);
    }

}