package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.syncdemo.controller.map.PetMappingService;
import com.github.vincemann.springrapid.syncdemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.ReadPetDto;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ReadPetDto> create(@RequestBody CreatePetDto dto) throws EntityNotFoundException, BadEntityException {
        Pet pet = petService.create(dto);
        return ResponseEntity.ok(mappingService.map(pet));
    }

    @GetMapping("find-some")
    public ResponseEntity<List<ReadPetDto>> findSome(@RequestBody List<Long> ids){
        List<Pet> pets = petService.findAllById(ids);
        return ResponseEntity.ok(mappingService.map(pets));
    }
    @GetMapping("find")
    public ResponseEntity<ReadPetDto> find(@RequestParam("name") String name){
        Optional<Pet> pet = petService.findByName(name);
        return ResponseEntity.ok(mappingService.map(pet.get()));
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
