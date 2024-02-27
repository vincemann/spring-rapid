package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Validate;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface MySignupService {

    public Owner signupOwner(@Valid SignupOwnerDto dto) throws BadEntityException;

    public Vet signupVet(@Valid SignupVetDto dto);
}
