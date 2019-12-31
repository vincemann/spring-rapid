package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.VetDto;
import io.github.vincemann.demo.repositories.VetRepository;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.VetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VetController extends DtoCrudController_SpringAdapter<Vet, VetDto,Long, VetRepository,VetService> {



    @RequestMapping({"/vets", "/vets/index", "/vets/index.html", "/vets.html"})
    public String listVets(Model model){
        model.addAttribute("vets", getCrudOnlyService().findAll());
        return "vets/index";
    }



}
