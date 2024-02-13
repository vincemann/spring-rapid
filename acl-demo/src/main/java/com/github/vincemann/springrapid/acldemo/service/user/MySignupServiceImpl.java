package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.model.User;
import com.github.vincemann.springrapid.acldemo.service.MyUserService;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.SignupServiceImpl;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
// overwriting of lib signup service only works via name not type
@Service(value = "signupService")
@Primary
public class MySignupServiceImpl extends SignupServiceImpl {

    private MyUserService userService;

    @Override
    public User signup(SignupDto dto) throws BadEntityException, AlreadyRegisteredException {
        User user = userService.createUser();
        user.getRoles().add(AuthRoles.USER);
        user.setUuid(UUID.randomUUID().toString());


        checkUniqueContactInformation(dto.getContactInformation());
        user.setContactInformation(dto.getContactInformation());

        // will be encoded by user service
        user.setPassword(dto.getPassword());

        User saved = userService.create(user);
        // is done in same transaction -> so applied directly, but message sent after transaction to make sure it
        // is not sent when transaction fails
        try {
            getVerificationService().makeUnverified(saved);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }

        log.debug("saved and send verification mail for unverified new user: " + saved);
        return saved;
    }

    @Autowired
    public void setUserService(MyUserService userService) {
        this.userService = userService;
    }
}
