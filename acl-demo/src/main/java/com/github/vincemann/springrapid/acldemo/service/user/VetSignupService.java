package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.auth.ex.BadEntityException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface VetSignupService {
    public Vet signup(@Valid SignupVetDto dto) throws BadEntityException;
}
