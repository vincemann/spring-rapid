package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public interface VerificationService<U extends AbstractUser> {

    void makeUnverified(U user);
    void makeVerified(U user);

    void sendVerificationMessage();
    void resendVerificationMessage();

    U verifyUser(String code);
}
