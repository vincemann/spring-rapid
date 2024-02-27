package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface OwnerSignupService {

    public Owner signup(@Valid SignupOwnerDto dto) throws BadEntityException;

}
