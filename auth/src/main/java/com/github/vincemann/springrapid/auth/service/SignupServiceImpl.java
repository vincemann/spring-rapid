package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.controller.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.core.PriorityOrdered;

import java.io.Serializable;
import java.util.HashSet;

public class SignupServiceImpl implements SignupService<AbstractUser, SignupDto> {

    private UserService<AbstractUser<?>,?> userService;
    private VerificationService verificationService;


    @Override
    public AbstractUser signup(SignupDto dto) throws BadEntityException, AlreadyRegisteredException {
        //admins get created with createAdminMethod
        AbstractUser user = userService.createUser();
        user.getRoles().add(AuthRoles.USER);


        checkUniqueContactInformation(dto.getContactInformation());
        user.setContactInformation(dto.getContactInformation());

        // will be encoded by user service
        user.setPassword(dto.getPassword());

        AbstractUser saved = userService.create(user);
        // is done in same transaction -> so applied directly, but message sent after transaction to make sure it
        // is not sent when transaction fails
        makeUnverified(saved);

        log.debug("saved and send verification mail for unverified new user: " + saved);

    }

    protected void checkUniqueContactInformation(String contactInformation){

    }
    @Override
    public AbstractUser signupAdmin(SignupDto signupDto) {
        checkUniqueContactInformation(admin.getContactInformation());
        passwordValidator.validate(admin.getPassword());
        return service.create(admin);
    }
}
