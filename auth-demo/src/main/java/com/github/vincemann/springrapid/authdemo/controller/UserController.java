package com.github.vincemann.springrapid.authdemo.controller;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.dto.user.FindOwnUserDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.authdemo.dto.*;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.service.MyJpaUserService;
import com.github.vincemann.springrapid.authdemo.service.MySignupService;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.io.IOException;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class UserController extends AbstractUserController<User, Long, MyUserService>  {

    private MySignupService signupService;

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {

        builder.when(endpoint(getUpdateUrl()).and(roles(AuthRoles.ADMIN)))
                        .thenReturn(MyFullUserDto.class);

        builder.when(endpoint(getUpdateUrl()))
                .thenReturn(UserUpdatesOwnDto.class);

        builder.when(direction(Direction.RESPONSE).and(principal(Principal.OWN)))
                .thenReturn(MyFindOwnUserDto.class);


        super.configureDtoMappings(builder);
    }

    @PostMapping(path = "/api/core/user/signup",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyFindOwnUserDto> signup(@Valid @RequestBody MySignupDto signupDto) throws BadEntityException, EntityNotFoundException, AlreadyRegisteredException {
        User saved = signupService.signup(signupDto);
        MyFindOwnUserDto dto = getDtoMapper().mapToDto(saved, MyFindOwnUserDto.class);
        return okWithAuthToken(dto);
    }

    // overwrite without annotations to not expose as endpoint
    @Override
    public ResponseEntity<FindOwnUserDto> signup(SignupDto signupDto) throws BadEntityException, IOException, EntityNotFoundException, AlreadyRegisteredException {
        return super.signup(signupDto);
    }

    @Autowired
    public void setSignupService(MySignupService signupService) {
        this.signupService = signupService;
    }
}