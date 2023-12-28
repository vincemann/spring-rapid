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

    private MyUserService injectedImpl;

    @Autowired
    public void setMyUserService(MyUserService myUserService) {
        this.injectedImpl = myUserService;
    }

    @Override
    protected DtoMappingContext provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
        return builder
                .forEndpoint(getSignupUrl(), Direction.REQUEST, SignupDto.class)
                .forEndpoint(getSignupUrl(), Direction.RESPONSE, UUIDSignupResponseDto.class)

                .withRoles(Roles.ADMIN)
                .forAll(FullUserDto.class)
                .build();
    }


    @Override
    public ResponseEntity<String> signup(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, IOException, EntityNotFoundException, AlreadyRegisteredException {
        injectedImpl.findById(42L);
        System.err.println("UserController::MyUserService " + injectedImpl);


        UserService<User, Long> unsecuredService = getUService();
        unsecuredService.findById(43L);
        System.err.println("AbstractUserController::UserService<U,Id> " + unsecuredService);
        // inject crudService sets this
//        MyUserService securedUserService = getSecuredUserService();
//        UserService<User, Long> genericService = getService();

        return super.signup(request, response);
    }
}