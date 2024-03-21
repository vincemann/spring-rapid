package com.github.vincemann.springrapid.acldemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.controller.map.OwnerMappingService;
import com.github.vincemann.springrapid.acldemo.dto.owner.OwnerReadsOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.user.OwnerSignupService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/api/core/owner/")
public class OwnerController{

    private OwnerSignupService signupService;
    private OwnerService service;
    private OwnerMappingService mappingService;


    @PostMapping("signup")
    public ResponseEntity<OwnerReadsOwnOwnerDto> signup(@Valid @RequestBody SignupOwnerDto dto) throws BadEntityException {
        Owner owner = signupService.signup(dto);
        return ResponseEntity.ok(mappingService.mapToReadOwnOwner(owner));
    }

    @PutMapping(value = "add-pet-spectator")
    public ResponseEntity<?> addPetSpectator(@RequestParam("permitted") long permittedOwnerId, @RequestParam("target") long targetOwnerId) throws EntityNotFoundException {
        service.addPetSpectator(permittedOwnerId, targetOwnerId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("update")
    public ResponseEntity<OwnerReadsOwnOwnerDto> update(@RequestBody UpdateOwnerDto dto) throws EntityNotFoundException {
        Owner owner = service.update(dto);
        return ResponseEntity.ok(mappingService.mapToReadOwnOwner(owner));
    }

    @GetMapping(value = "find-by-name")
    public ResponseEntity<Object> findByName(@RequestParam("name") String name, HttpServletRequest request) throws EntityNotFoundException, BadEntityException, JsonProcessingException {
        Optional<Owner> owner = service.findByLastName(name);
        VerifyEntity.isPresent(owner,name,Owner.class);
        return ResponseEntity.ok(mappingService.mapToReadOwnerDto(owner.get()));
    }


    @Autowired
    public void setSignupService(OwnerSignupService signupService) {
        this.signupService = signupService;
    }

    @Autowired
    public void setMappingService(OwnerMappingService mappingService) {
        this.mappingService = mappingService;
    }

    @Autowired
    @Secured
    public void setOwnerService(OwnerService ownerService) {
        this.service = ownerService;
    }
}
