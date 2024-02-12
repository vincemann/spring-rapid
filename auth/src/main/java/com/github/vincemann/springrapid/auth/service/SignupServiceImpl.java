package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Getter
public class SignupServiceImpl implements SignupService {

    private UserService<AbstractUser<?>,?> userService;
    private VerificationService verificationService;


    @Override
    public AbstractUser signup(SignupDto dto) throws BadEntityException, AlreadyRegisteredException {
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


    protected void checkUniqueContactInformation(String contactInformation) throws AlreadyRegisteredException {
        if (userService.findByContactInformation(contactInformation).isPresent())
            throw new AlreadyRegisteredException("contact information already present");
    }


    @Autowired
    public void setUserService(UserService<AbstractUser<?>, ?> userService) {
        this.userService = userService;
    }

    @Autowired
    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }
}
