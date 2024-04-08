package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.controller.map.PetMappingService;
import com.github.vincemann.springrapid.acldemo.dto.pet.*;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.auth.ex.BadEntityException;
import com.github.vincemann.springrapid.auth.ex.EntityNotFoundException;
import com.github.vincemann.springrapid.auth.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller
@RequestMapping("/api/core/pet/")
public class PetController {

    private PetService petService;
    private PetMappingService mappingService;

    @PostMapping("create")
    public ResponseEntity<OwnerReadsOwnPetDto> create(@RequestBody CreatePetDto dto) throws EntityNotFoundException, BadEntityException {
        Pet pet = petService.create(dto);
        return ResponseEntity.ok(mappingService.mapToOwnerReadsOwnPetDto(pet));
    }

    @GetMapping("find")
    public ResponseEntity<Object> find(@RequestParam("name") String name) throws EntityNotFoundException {
        Optional<Pet> pet = petService.findByName(name);
        VerifyEntity.isPresent(pet,name,Pet.class);
        Object dto = mappingService.mapToReadPetDto(pet.get());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("add-illness")
    public ResponseEntity<VetReadsPetDto> addIllness(@RequestBody UpdateIllnessDto dto) throws EntityNotFoundException, BadEntityException {
        Pet pet = petService.addIllnesses(dto);
        return ResponseEntity.ok(mappingService.mapToVetReadsPetDto(pet));
    }

    @PutMapping("remove-illness")
    public ResponseEntity<VetReadsPetDto> removeIllness(@RequestBody UpdateIllnessDto dto) throws EntityNotFoundException, BadEntityException {
        Pet pet = petService.removeIllness(dto);
        return ResponseEntity.ok(mappingService.mapToVetReadsPetDto(pet));
    }

    @PutMapping("owner-update")
    public ResponseEntity<OwnerReadsOwnPetDto> ownerUpdate(@RequestBody OwnerUpdatesPetDto dto) throws EntityNotFoundException, BadEntityException {
        Pet pet = petService.ownerUpdatesPet(dto);
        return ResponseEntity.ok(mappingService.mapToOwnerReadsOwnPetDto(pet));
    }

    @Autowired
    @Secured
    public void setPetService(PetService petService) {
        this.petService = petService;
    }

    @Autowired
    public void setMappingService(PetMappingService mappingService) {
        this.mappingService = mappingService;
    }
}
