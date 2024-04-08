package com.github.vincemann.springrapid.acl;

import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.service.*;
import org.springframework.beans.factory.annotation.Autowired;

public class AclUserController<S extends UserService<?,?>>
        extends AbstractUserController<S> {


    @Autowired
    @Secured
    public void setUserService(S userService) {
        super.setUserService(userService);
    }

    @Autowired
    @Secured
    public void setPasswordService(PasswordService passwordService) {
        super.setPasswordService(passwordService);
    }

    @Autowired
    @Secured
    public void setContactInformationService(ContactInformationService contactInformationService) {
        super.setContactInformationService(contactInformationService);
    }

    @Autowired
    @Secured
    public void setVerificationService(VerificationService verificationService) {
        super.setVerificationService(verificationService);
    }
}
