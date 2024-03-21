package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.syncdemo.controller.map.OwnerMappingService;
import com.github.vincemann.springrapid.syncdemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnerDto;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/api/core/owner/")
public class OwnerController {


    @Autowired
    private OwnerService service;
    @Autowired
    private OwnerMappingService mappingService;

    @PostMapping("create")
    public ReadOwnerDto create(CreateOwnerDto dto) throws EntityNotFoundException {
        Owner owner = service.create(dto);
        return mappingService.map(owner);
    }
    @GetMapping("find")
    public ReadOwnerDto find(@RequestParam("id") long id) throws EntityNotFoundException {
        Optional<Owner> byId = service.find(id);
        VerifyEntity.isPresent(byId.get(),id,Owner.class);
        return mappingService.map(byId.get());
    }



}
