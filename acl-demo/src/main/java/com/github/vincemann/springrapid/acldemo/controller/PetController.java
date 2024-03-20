package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.controller.map.PetMappingService;
import com.github.vincemann.springrapid.acldemo.dto.pet.*;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller
@RequestMapping("/api/core/pet/")
public class PetController {

    private PetService petService;
    private PetMappingService mappingService;

    @PostMapping("create")
    public OwnerReadsOwnPetDto create(@RequestBody CreatePetDto dto) throws EntityNotFoundException {
        Pet pet = petService.create(dto);
        return mappingService.mapToOwnerReadsOwnPetDto(pet);
    }

    @GetMapping("find")
    public Object find(@RequestParam("name") String name){
        Optional<Pet> pet = petService.findByName(name);
        return mappingService.mapToReadPetDto(pet.get());
    }

    @PutMapping("vet-update")
    public VetReadsPetDto vetUpdate(@RequestBody VetUpdatesPetDto dto) throws EntityNotFoundException {
        Pet pet = petService.vetUpdatesPet(dto);
        return mappingService.mapToVetReadsPetDto(pet);
    }

    @PutMapping("owner-update")
    public OwnerReadsOwnPetDto ownerUpdate(@RequestBody OwnerUpdatesPetDto dto) throws EntityNotFoundException, BadEntityException {
        Pet pet = petService.ownerUpdatesPet(dto);
        return mappingService.mapToOwnerReadsOwnPetDto(pet);
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
