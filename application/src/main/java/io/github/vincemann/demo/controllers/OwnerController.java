package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.OwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.MappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OwnerController
        extends DtoCrudController_SpringAdapter<Owner, Long,OwnerRepository> {


    public OwnerController() {
        super(MappingContext.DEFAULT(OwnerDto.class));
    }

    @RequestMapping("/owners")
    public String listOwners(Model model){
        model.addAttribute("owners", getCrudService().findAll());
        return "owners/index";
    }

}
