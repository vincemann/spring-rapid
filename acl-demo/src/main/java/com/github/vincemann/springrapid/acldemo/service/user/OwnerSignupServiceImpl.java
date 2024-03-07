package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.service.VerificationService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.google.common.collect.Sets;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "signupService")  // disables autoconfiguring spring rapids SignupServiceImpl bean
public class OwnerSignupServiceImpl implements OwnerSignupService {

    private OwnerService ownerService;
    private VerificationService verificationService;

    @Override
    public Owner signup(SignupOwnerDto dto) throws BadEntityException {
        ModelMapper mapper = new ModelMapper();
        Owner owner = mapper.map(dto, Owner.class);
        owner.setRoles(Sets.newHashSet(Roles.OWNER, Roles.USER));

        Owner saved = ownerService.create(owner);
        try {
            verificationService.makeUnverified(saved);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
        return saved;
    }

    @Autowired
    @Root
    public void setOwnerService(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Autowired
    @Root
    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }
}
