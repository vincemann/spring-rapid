package com.github.vincemann.springrapid.authdemo.controller;

import com.github.vincemann.springrapid.acl.AclUserController;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.authdemo.dto.ReadUserDto;
import com.github.vincemann.springrapid.authdemo.dto.SignupDto;
import com.github.vincemann.springrapid.authdemo.User;
import com.github.vincemann.springrapid.authdemo.service.MySignupService;
import com.github.vincemann.springrapid.authdemo.service.MyUserService;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@Controller
public class UserController extends AclUserController<MyUserService> {

    private MySignupService signupService;
    private UserMappingService mappingService;


    @PostMapping(path = "/api/core/user/signup")
    public ResponseEntity<ReadUserDto> signup(@Valid @RequestBody SignupDto signupDto) throws BadEntityException, EntityNotFoundException, AlreadyRegisteredException {
        User user = signupService.signup(signupDto);
        ReadUserDto dto = mappingService.map(user);
        return okWithToken(dto,user.getContactInformation());
    }


    @Autowired
    public void setSignupService(MySignupService signupService) {
        this.signupService = signupService;
    }

    @Autowired
    public void setMappingService(UserMappingService mappingService) {
        this.mappingService = mappingService;
    }
}