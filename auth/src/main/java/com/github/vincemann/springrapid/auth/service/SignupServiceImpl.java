package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.controller.dto.SignupAdminDto;
import com.github.vincemann.springrapid.auth.controller.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.util.TransactionalUtils;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashSet;

@Slf4j
public class SignupServiceImpl implements SignupService {

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
        try {
            verificationService.makeUnverified(saved);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }

        log.debug("saved and send verification mail for unverified new user: " + saved);
        return saved;
    }

    protected void checkUniqueContactInformation(String contactInformation) throws BadEntityException {
        if (userService.findByContactInformation(contactInformation).isPresent())
            throw new BadEntityException("contact information already present");
    }

    @Override
    public AbstractUser signupAdmin(SignupAdminDto signupDto) throws BadEntityException {
        checkUniqueContactInformation(signupDto.getContactInformation());
        AbstractUser user = userService.createUser();
        user.getRoles().add(AuthRoles.ADMIN);
        return userService.create(user);
    }
}
