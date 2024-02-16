package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
public interface VerificationService {


    AbstractUser makeUnverified(AbstractUser user) throws BadEntityException, EntityNotFoundException;

    AbstractUser makeVerified(AbstractUser user) throws BadEntityException, EntityNotFoundException;

    AbstractUser resendVerificationMessage(@NotBlank String contactInformation) throws EntityNotFoundException, BadEntityException;

    AbstractUser verifyUser(@NotBlank String code) throws EntityNotFoundException, BadTokenException, BadEntityException;
}
