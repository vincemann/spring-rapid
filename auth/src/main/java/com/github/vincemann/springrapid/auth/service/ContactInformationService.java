package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Validated
public interface ContactInformationService {

    public AbstractUser changeContactInformation(@NotBlank String code) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException, BadTokenException;

    public void requestContactInformationChange(@Valid RequestContactInformationChangeDto dto) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException;
}
