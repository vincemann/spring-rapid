package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.controller.map.PetMappingService;
import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerReadsOwnPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.UpdatePetsIllnessesDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.VetReadsPetDto;
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

    @PutMapping("update-illnesses")
    public VetReadsPetDto updatePetsIllnesses(@RequestBody UpdatePetsIllnessesDto dto) throws EntityNotFoundException {
        Pet pet = petService.updateIllnesses(dto);
        return mappingService.mapToVetReadsPetDto(pet);
    }

    @PutMapping("update-name")
    public void updatePetsName(@RequestParam("old") String oldName, @RequestParam("new") String newName) throws EntityNotFoundException, BadEntityException {
        petService.updateName(oldName,newName);
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
