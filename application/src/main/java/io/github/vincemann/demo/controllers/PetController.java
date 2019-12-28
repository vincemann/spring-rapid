package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.PetDto;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import org.springframework.stereotype.Controller;


@Controller
public class PetController
        extends DtoCrudController_SpringAdapter<Pet, PetDto,Long, PetRepository, PetService> {

}
