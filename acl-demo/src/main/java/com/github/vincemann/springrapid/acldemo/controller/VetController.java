package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.controller.map.VetMappingService;
import com.github.vincemann.springrapid.acldemo.dto.vet.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.service.user.VetSignupService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/api/core/vet/")
public class VetController {

    private VetSignupService signupService;
    private VetMappingService mappingService;



    @PostMapping("signup")
    public ReadVetDto signup(@Valid @RequestBody SignupVetDto dto) throws BadEntityException {
        Vet vet = signupService.signup(dto);
        return mappingService.map(vet);
    }


    @Autowired
    public void setSignupService(VetSignupService signupService) {
        this.signupService = signupService;
    }

    @Autowired
    public void setMappingService(VetMappingService mappingService) {
        this.mappingService = mappingService;
    }



}
