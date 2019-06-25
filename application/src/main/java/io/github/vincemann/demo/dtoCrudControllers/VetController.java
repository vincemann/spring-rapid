package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdatper;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import io.github.vincemann.demo.dtos.VetDTO;
import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.VetService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import io.github.vincemann.generic.crud.lib.dtoMapper.BasicDTOMapper;
import io.github.vincemann.generic.crud.lib.dtoMapper.DTOMapper;
import io.github.vincemann.generic.crud.lib.service.EndpointService;

@Controller
public class VetController extends DTOCrudControllerSpringAdatper<Vet, VetDTO,Long,VetService> {

    public VetController(VetService crudService, EndpointService endpointService, MediaTypeStrategy mediaTypeStrategy, IdFetchingStrategy<Long> longIdFetchingStrategy) {
        super(crudService,
                endpointService,
                Vet.class,
                VetDTO.class,
                longIdFetchingStrategy,
                mediaTypeStrategy,
                new JavaXValidationStrategy<>());
    }


    @Override
    protected DTOMapper<Vet, VetDTO, Long> provideServiceEntityToDTOMapper() {
        return new BasicDTOMapper<>(VetDTO.class,new ModelMapper());
    }

    @Override
    protected DTOMapper<VetDTO, Vet, Long> provideDTOToServiceEntityMapper() {
        return new BasicDTOMapper<>(Vet.class,new ModelMapper());
    }


    @RequestMapping({"/vets", "/vets/index", "/vets/index.html", "/vets.html"})
    public String listVets(Model model){

        model.addAttribute("vets", getCrudService().findAll());

        return "vets/index";
    }



}
