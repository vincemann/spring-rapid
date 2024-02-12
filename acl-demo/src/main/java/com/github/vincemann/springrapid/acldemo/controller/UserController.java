package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.dto.user.MyFullUserDto;
import com.github.vincemann.springrapid.acldemo.dto.user.UUIDSignupResponseDto;
import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.dto.user.FindOwnUserDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
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


    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(roles(AuthRoles.ADMIN))
                .thenReturn(MyFullUserDto.class);


        super.configureDtoMappings(builder);
    }

    @PostMapping(path = "/api/core/user/signup",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUIDSignupResponseDto> signupUser(@Valid @RequestBody SignupDto signupDto) throws BadEntityException, EntityNotFoundException, AlreadyRegisteredException {
        AbstractUser saved = getSignupService().signup(signupDto);
        UUIDSignupResponseDto dto = getDtoMapper().mapToDto(saved, UUIDSignupResponseDto.class);
        return okWithAuthToken(dto);
    }

    // overwrite without annotations to not expose as endpoint
    @Override
    public ResponseEntity<FindOwnUserDto> signup(SignupDto signupDto) throws BadEntityException, IOException, EntityNotFoundException, AlreadyRegisteredException {
        return super.signup(signupDto);
    }
}