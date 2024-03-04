package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.VetService;
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
        vet.setRoles(Sets.newHashSet(MyRoles.VET,MyRoles.USER));

        Vet saved = vetService.create(vet);
        try {
            verificationService.makeUnverified(saved);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
        return saved;
    }

    @Autowired
    public void setVetService(VetService vetService) {
        this.vetService = vetService;
    }

    @com.github.vincemann.springrapid.acldemo.Vet
    @Autowired
    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }
}
