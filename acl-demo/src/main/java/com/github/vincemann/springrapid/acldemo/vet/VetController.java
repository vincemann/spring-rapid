package com.github.vincemann.springrapid.acldemo.vet;

import com.github.vincemann.springrapid.acldemo.vet.dto.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.vet.dto.SignupVetDto;
import com.github.vincemann.springrapid.auth.BadEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/core/vet/")
public class VetController {

    private VetSignupService signupService;
    private VetMappingService mappingService;



    @PostMapping("signup")
    public ResponseEntity<ReadVetDto> signup(@Valid @RequestBody SignupVetDto dto) throws BadEntityException {
        Vet vet = signupService.signup(dto);
        return ResponseEntity.ok(mappingService.map(vet));
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
