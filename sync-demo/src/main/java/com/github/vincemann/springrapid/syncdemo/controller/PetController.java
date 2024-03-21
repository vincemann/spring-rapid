package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.syncdemo.controller.map.PetMappingService;
import com.github.vincemann.springrapid.syncdemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.ReadPetDto;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/api/core/pet/")
public class PetController {

    private PetService petService;
    private PetMappingService mappingService;

    @PostMapping("create")
    public ReadPetDto create(@RequestBody CreatePetDto dto) throws EntityNotFoundException, BadEntityException {
        Pet pet = petService.create(dto);
        return mappingService.map(pet);
    }

    @GetMapping("find-some")
    public List<ReadPetDto> findSome(@RequestBody List<Long> ids){
        List<Pet> pets = petService.findAllById(ids);
        return mappingService.map(pets);
    }
    @GetMapping("find")
    public ReadPetDto find(@RequestParam("name") String name){
        Optional<Pet> pet = petService.findByName(name);
        return mappingService.map(pet.get());
    }

    @Autowired
    public void setPetService(PetService petService) {
        this.petService = petService;
    }

    @Autowired
    public void setMappingService(PetMappingService mappingService) {
        this.mappingService = mappingService;
    }


}
