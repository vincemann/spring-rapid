package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.dto.user.FullUserDto;
import com.github.vincemann.springrapid.acldemo.dto.user.UUIDSignupResponseDto;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.controller.UserDtoMappingContextBuilder;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class UserController extends AbstractUserController<User, Long, MyUserService>  {

    @Override
    protected DtoMappingContext provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
        return builder
                .forEndpoint(getSignupUrl(), Direction.REQUEST, SignupDto.class)
                .forEndpoint(getSignupUrl(), Direction.RESPONSE, UUIDSignupResponseDto.class)

                .withRoles(Roles.ADMIN)
                .forAll(FullUserDto.class)
                
                .build();
    }
}