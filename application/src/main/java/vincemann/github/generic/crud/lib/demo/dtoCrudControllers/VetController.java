package vincemann.github.generic.crud.lib.demo.dtoCrudControllers;

import vincemann.github.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdatper;
import vincemann.github.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import vincemann.github.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import vincemann.github.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import vincemann.github.generic.crud.lib.demo.dtos.VetDTO;
import vincemann.github.generic.crud.lib.demo.model.Vet;
import vincemann.github.generic.crud.lib.demo.service.VetService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import vincemann.github.generic.crud.lib.dtoMapper.BasicDTOMapper;
import vincemann.github.generic.crud.lib.dtoMapper.DTOMapper;
import vincemann.github.generic.crud.lib.service.EndpointService;

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
