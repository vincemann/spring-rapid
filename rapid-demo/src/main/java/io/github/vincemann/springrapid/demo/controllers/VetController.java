package io.github.vincemann.springrapid.demo.controllers;

import io.github.vincemann.springrapid.demo.dtos.VetDto;
import io.github.vincemann.springrapid.demo.model.Vet;
import io.github.vincemann.springrapid.core.slicing.components.WebController;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.springrapid.core.controller.springAdapter.SpringAdapterJsonDtoCrudController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@WebController
public class VetController
        extends SpringAdapterJsonDtoCrudController<Vet, Long> {

    public VetController() {
        super(DtoMappingContextBuilder.builder()
                .forAll(VetDto.class)
                .build()
        );
    }

    @RequestMapping({"/vets", "/vets/index", "/vets/index.html", "/vets.html"})
    public String listVets(Model model){
        model.addAttribute("vets", getCrudService().findAll());
        return "vets/index";
    }



}
