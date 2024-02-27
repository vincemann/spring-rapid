package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface VetSignupService {
    public Vet signup(@Valid SignupVetDto dto) throws BadEntityException;
}
