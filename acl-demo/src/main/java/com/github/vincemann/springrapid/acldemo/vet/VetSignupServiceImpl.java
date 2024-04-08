package com.github.vincemann.springrapid.acldemo.vet;

import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.vet.dto.SignupVetDto;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.service.VerificationService;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class VetSignupServiceImpl implements VetSignupService {

    private VetService vetService;
    private VerificationService verificationService;

    @Override
    public Vet signup(SignupVetDto dto) throws BadEntityException {
        ModelMapper mapper = new ModelMapper();
        Vet vet = mapper.map(dto, Vet.class);
        vet.setRoles(new HashSet<>(Arrays.asList(Roles.VET, Roles.USER)));

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
