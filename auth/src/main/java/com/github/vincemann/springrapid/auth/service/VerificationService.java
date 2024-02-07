package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public interface VerificationService {


    @Transactional
    AbstractUser makeUnverified(AbstractUser user) throws BadEntityException, EntityNotFoundException;

    AbstractUser makeVerified(AbstractUser user) throws BadEntityException, EntityNotFoundException;

    void resendVerificationMessage(AbstractUser user) throws EntityNotFoundException, BadEntityException;

    AbstractUser verifyUser(String code) throws EntityNotFoundException, BadTokenException, BadEntityException;
}
