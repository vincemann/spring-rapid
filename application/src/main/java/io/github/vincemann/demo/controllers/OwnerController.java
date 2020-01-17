package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.owner.CreateOwnerDto;
import io.github.vincemann.demo.dtos.owner.ReadOwnerDto;
import io.github.vincemann.demo.dtos.owner.UpdateOwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OwnerController
        extends SpringAdapterDtoCrudController<Owner, Long> {


    public OwnerController() {
        super(DtoMappingContext.CREATE_UPDATE_READ(
                CreateOwnerDto.class,
                UpdateOwnerDto.class,
                ReadOwnerDto.class
        ));
    }

    @RequestMapping("/owners")
    public String listOwners(Model model){
        model.addAttribute("owners", getCrudService().findAll());
        return "owners/index";
    }

}
