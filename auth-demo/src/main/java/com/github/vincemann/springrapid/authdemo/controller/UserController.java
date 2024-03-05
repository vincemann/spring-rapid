package com.github.vincemann.springrapid.authdemo.controller;

import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.authdemo.dto.*;
import com.github.vincemann.springrapid.authdemo.dto.user.MyReadOwnUserDto;
import com.github.vincemann.springrapid.authdemo.dto.user.MyFullUserDto;
import com.github.vincemann.springrapid.authdemo.dto.user.UserUpdatesOwnDto;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authdemo.service.MySignupService;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

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
                .thenReturn(MyReadOwnUserDto.class);


        super.configureDtoMappings(builder);
    }

    @PostMapping(path = "/api/core/user/signup",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MyReadOwnUserDto> signup(@Valid @RequestBody MySignupDto signupDto) throws BadEntityException, EntityNotFoundException, AlreadyRegisteredException {
        User saved = signupService.signup(signupDto);
        MyReadOwnUserDto dto = getDtoMapper().mapToDto(saved, MyReadOwnUserDto.class);
        return okWithAuthToken(dto,saved.getContactInformation());
    }

    @Override
    public List<String> getIgnoredEndPoints() {
        return Lists.newArrayList(getSignupUrl());
    }

    @Autowired
    public void setSignupService(MySignupService signupService) {
        this.signupService = signupService;
    }
}