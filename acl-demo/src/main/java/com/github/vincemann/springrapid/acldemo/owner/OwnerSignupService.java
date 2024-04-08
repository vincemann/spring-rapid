package com.github.vincemann.springrapid.acldemo.owner;

import com.github.vincemann.springrapid.acldemo.owner.dto.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.owner.Owner;
import com.github.vincemann.springrapid.auth.BadEntityException;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Validated
public interface OwnerSignupService {

    public Owner signup(@Valid SignupOwnerDto dto) throws BadEntityException;

}
