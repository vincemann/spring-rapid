package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.val.ContactInformationValidator;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidator;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Getter
public class SignupServiceImpl implements SignupService {

    private UserService<AbstractUser<?>,?> userService;
    private VerificationService verificationService;
    private PasswordValidator passwordValidator;
    private ContactInformationValidator contactInformationValidator;
    private RapidAclService aclService;



    @Transactional
    @Override
    public AbstractUser signup(SignupDto dto) throws BadEntityException, AlreadyRegisteredException {
        VerifyEntity.notEmpty(dto.getContactInformation(),"contact-information");
        VerifyEntity.notEmpty(dto.getPassword(),"password");

        contactInformationValidator.validate(dto.getContactInformation());
        passwordValidator.validate(dto.getPassword()); // fail fast

        checkUniqueContactInformation(dto.getContactInformation());


        AbstractUser user = userService.createUser();
        user.getRoles().add(AuthRoles.USER);
        user.setContactInformation(dto.getContactInformation());
        user.setPassword(dto.getPassword()); // will be encoded by user service downstream

        AbstractUser saved = userService.create(user);
        // is done in same transaction -> so applied directly, but message is sent after transaction to make sure it
        // is not sent when transaction fails
        try {
            verificationService.makeUnverified(saved);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }

        log.debug("saved and send verification mail for unverified new user: " + saved);
        saveAclInfo(saved);
        return saved;
    }

    protected void saveAclInfo(AbstractUser saved){
        aclService.grantUserPermissionForEntity(saved.getContactInformation(),saved, BasePermission.ADMINISTRATION);
    }


    protected void checkUniqueContactInformation(String contactInformation) throws AlreadyRegisteredException {
        if (userService.findByContactInformation(contactInformation).isPresent())
            throw new AlreadyRegisteredException("contact information already present");
    }


    @Autowired public void setUserService(UserService<AbstractUser<?>, ?> userService) {
        this.userService = userService;
    }
    @Autowired public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @Autowired
    public void setPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    @Autowired
    public void setAclService(RapidAclService aclService) {
        this.aclService = aclService;
    }

    @Autowired
    public void setContactInformationValidator(ContactInformationValidator contactInformationValidator) {
        this.contactInformationValidator = contactInformationValidator;
    }
}
