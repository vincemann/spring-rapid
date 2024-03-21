package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.syncdemo.controller.map.OwnerMappingService;
import com.github.vincemann.springrapid.syncdemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.ReadPetDto;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/core/owner/")
public class OwnerController {


    @Autowired
    private OwnerService service;
    @Autowired
    private OwnerMappingService mappingService;

    @PostMapping("create")
    public ResponseEntity<ReadOwnerDto> create(@RequestBody CreateOwnerDto dto) throws EntityNotFoundException {
        Owner owner = service.create(dto);
        return ResponseEntity.ok(mappingService.map(owner));
    }
    @GetMapping("find")
    public ResponseEntity<ReadOwnerDto> find(@RequestParam("id") long id) throws EntityNotFoundException {
        Optional<Owner> byId = service.find(id);
        VerifyEntity.isPresent(byId.get(),id,Owner.class);
        return ResponseEntity.ok(mappingService.map(byId.get()));
    }

    @GetMapping("find-some")
    public ResponseEntity<List<ReadOwnerDto>> findSome(@RequestBody List<Long> ids){
        List<Owner> owners = service.findAllById(ids);
        return ResponseEntity.ok(mappingService.map(owners));
    }



}
