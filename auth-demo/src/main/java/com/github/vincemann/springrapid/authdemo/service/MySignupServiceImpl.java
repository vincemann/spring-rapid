package com.github.vincemann.springrapid.authdemo.service;

import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.Roles;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.VerificationService;
import com.github.vincemann.springrapid.authdemo.dto.SignupDto;
import com.github.vincemann.springrapid.authdemo.User;
import com.github.vincemann.springrapid.auth.ex.BadEntityException;
import com.github.vincemann.springrapid.auth.ex.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MySignupServiceImpl implements MySignupService {


    private MyUserService userService;
    private VerificationService verificationService;

    @Override
    public User signup(SignupDto dto) throws BadEntityException, AlreadyRegisteredException {
        //admins get created with createAdminMethod
        User user = userService.createUser();
        user.getRoles().add(AuthRoles.USER);


        checkUniqueContactInformation(dto.getContactInformation());
        user.setContactInformation(dto.getContactInformation());
        user.setName(dto.getName());

        // will be encoded by user service
        user.setPassword(dto.getPassword());

        User saved = userService.create(user);
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


    protected void checkUniqueContactInformation(String contactInformation) throws AlreadyRegisteredException {
        if (userService.findByContactInformation(contactInformation).isPresent())
            throw new AlreadyRegisteredException("contact information already present");
    }

    @Autowired
    @Root
    public void setUserService(MyUserService userService) {
        this.userService = userService;
    }

    @Autowired
    @Root
    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }
}
