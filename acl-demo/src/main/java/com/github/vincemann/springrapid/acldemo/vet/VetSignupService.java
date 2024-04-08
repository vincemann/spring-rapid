package com.github.vincemann.springrapid.acldemo.vet;

import com.github.vincemann.springrapid.acldemo.vet.dto.SignupVetDto;
import com.github.vincemann.springrapid.auth.BadEntityException;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Validated
public interface VetSignupService {
    public Vet signup(@Valid SignupVetDto dto) throws BadEntityException;
}
