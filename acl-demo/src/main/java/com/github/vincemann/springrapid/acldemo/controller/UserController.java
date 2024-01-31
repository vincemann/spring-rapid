package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.dto.user.FullUserDto;
import com.github.vincemann.springrapid.acldemo.dto.user.UUIDSignupResponseDto;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.controller.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class UserController extends AbstractUserController<User, Long>  {


    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {

        builder.when(roles(AuthRoles.ADMIN))
                .thenReturn(FullUserDto.class);

        builder.when(endpoint(getSignupUrl())
                        .and(direction(Direction.REQUEST)))
                .thenReturn(SignupDto.class);

        builder.when(endpoint(getSignupUrl())
                        .and(direction(Direction.RESPONSE)))
                .thenReturn(UUIDSignupResponseDto.class);


        super.configureDtoMappings(builder);
    }
}