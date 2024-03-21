package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.syncdemo.controller.map.OwnerMappingService;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnerDto;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.repo.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/api/core/owner/")
public class OwnerController {



    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private OwnerMappingService mappingService;
    @GetMapping("find")
    public ReadOwnerDto find(@RequestParam("id") long id) throws EntityNotFoundException {
        Optional<Owner> byId = ownerRepository.findById(id);
        VerifyEntity.isPresent(byId.get(),id,Owner.class);
        return mappingService.map(byId.get());
    }



}
