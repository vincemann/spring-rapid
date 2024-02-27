package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.google.common.collect.Sets;

public class VetSignupServiceImpl implements VetSignupService {
    @Override
    public Vet signup(SignupVetDto dto) throws BadEntityException {
        Vet owner = Vet.builder()
                .roles(Sets.newHashSet(MyRoles.VET,MyRoles.USER))
                .build();

        Owner saved = ownerService.create(owner);
        try {
            verificationService.makeUnverified(saved);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
        return saved;
    }
}
