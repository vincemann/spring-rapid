package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.service.VerificationService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.google.common.collect.Sets;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VetSignupServiceImpl implements VetSignupService {

    private VetService vetService;
    private VerificationService verificationService;

    @Override
    public Vet signup(SignupVetDto dto) throws BadEntityException {
        ModelMapper mapper = new ModelMapper();
        Vet vet = mapper.map(dto, Vet.class);
        vet.setRoles(Sets.newHashSet(Roles.VET, Roles.USER));

        Vet saved = vetService.create(vet);
        try {
            verificationService.makeUnverified(saved);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
        return saved;
    }

    @Autowired
    @Root
    public void setUserService(VetService userService) {
        this.vetService = userService;
    }

    @Autowired
    @Root
    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }
}
