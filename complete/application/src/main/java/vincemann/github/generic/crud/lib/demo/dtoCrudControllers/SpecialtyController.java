package vincemann.github.generic.crud.lib.demo.dtoCrudControllers;

import vincemann.github.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdatper;
import vincemann.github.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import vincemann.github.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.JSONMediaTypeStrategy;
import vincemann.github.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import vincemann.github.generic.crud.lib.demo.dtos.SpecialtyDTO;
import vincemann.github.generic.crud.lib.demo.model.Specialty;
import vincemann.github.generic.crud.lib.demo.service.SpecialtyService;
import org.springframework.stereotype.Controller;
import vincemann.github.generic.crud.lib.dtoMapper.BasicDTOMapper;
import vincemann.github.generic.crud.lib.dtoMapper.DTOMapper;
import vincemann.github.generic.crud.lib.service.EndpointService;

@Controller
public class SpecialtyController extends DTOCrudControllerSpringAdatper<Specialty, SpecialtyDTO,Long, SpecialtyService> {


    public SpecialtyController(SpecialtyService crudService, EndpointService endpointService, IdFetchingStrategy<Long> longIdFetchingStrategy) {
        super(crudService,
                endpointService,
                Specialty.class,
                SpecialtyDTO.class,
                longIdFetchingStrategy,
                new JSONMediaTypeStrategy(),
                new JavaXValidationStrategy<>());
    }

    @Override
    protected DTOMapper<Specialty, SpecialtyDTO, Long> provideServiceEntityToDTOMapper() {
        return new BasicDTOMapper<>(SpecialtyDTO.class);
    }

    @Override
    protected DTOMapper<SpecialtyDTO, Specialty, Long> provideDTOToServiceEntityMapper() {
        return new BasicDTOMapper<>(Specialty.class);
    }
}
