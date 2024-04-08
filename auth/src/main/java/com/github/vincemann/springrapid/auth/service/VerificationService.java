package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.jwt.BadTokenException;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;

public interface VerificationService {


    AbstractUser makeUnverified(AbstractUser user) throws BadEntityException, EntityNotFoundException;

    AbstractUser makeVerified(AbstractUser user) throws BadEntityException, EntityNotFoundException;

    AbstractUser resendVerificationMessage(String contactInformation) throws EntityNotFoundException, BadEntityException;

    AbstractUser verifyUser(String code) throws EntityNotFoundException, BadTokenException, BadEntityException;
}
