package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.VetDto;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.EndpointsExposureDetails;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.VetService;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;

@Controller
public class VetController extends DtoCrudController_SpringAdapter<Vet, VetDto,Long,VetService> {

    public VetController(VetService crudService, MediaTypeStrategy mediaTypeStrategy, IdFetchingStrategy<Long> longIdFetchingStrategy, ValidationStrategy validationStrategy, DtoMapper dtoMapper, EndpointsExposureDetails endpointsExposureDetails, EndpointService endpointService) {
        super(crudService,
                endpointService,
                longIdFetchingStrategy,
                mediaTypeStrategy,
                validationStrategy,
                dtoMapper,
                endpointsExposureDetails
        );
    }



    @RequestMapping({"/vets", "/vets/index", "/vets/index.html", "/vets.html"})
    public String listVets(Model model){
        model.addAttribute("vets", getCrudService().findAll());
        return "vets/index";
    }



}
