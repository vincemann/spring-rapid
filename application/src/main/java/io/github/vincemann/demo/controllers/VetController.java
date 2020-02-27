package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.VetDto;
import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.generic.crud.lib.config.WebComponent;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterDtoCrudController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@WebComponent
public class VetController
        extends SpringAdapterDtoCrudController<Vet, Long> {

    public VetController() {
        super(DtoMappingContext.DEFAULT(VetDto.class));
    }

    @RequestMapping({"/vets", "/vets/index", "/vets/index.html", "/vets.html"})
    public String listVets(Model model){
        model.addAttribute("vets", getCrudService().findAll());
        return "vets/index";
    }



}
