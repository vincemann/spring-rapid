package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.auth.msg.mail.SmtpMailSender;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.val.ContactInformationValidator;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidator;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;



public class SignupServiceImpl implements SignupService {

    private final Log log = LogFactory.getLog(SignupServiceImpl.class);

    private UserService userService;
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


    protected UserService<AbstractUser<?>, ?> getUserService() {
        return userService;
    }

    protected VerificationService getVerificationService() {
        return verificationService;
    }

    protected PasswordValidator getPasswordValidator() {
        return passwordValidator;
    }

    protected ContactInformationValidator getContactInformationValidator() {
        return contactInformationValidator;
    }

    protected RapidAclService getAclService() {
        return aclService;
    }

    @Root
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    @Root
    public void setVerificationService(VerificationService verificationService) {
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
