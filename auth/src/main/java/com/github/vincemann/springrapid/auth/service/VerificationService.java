package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

public interface VerificationService<U extends AbstractUser> {

    void makeUnverified(U user) throws BadEntityException, EntityNotFoundException;
    void makeVerified(U user) throws BadEntityException, EntityNotFoundException;

    void sendVerificationMessage();

    void sendVerificationMessage(U user);


    U verifyUser(String code);
}
