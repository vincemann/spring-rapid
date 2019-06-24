package vincemann.github.generic.crud.lib.demo.dtoCrudControllers;

import vincemann.github.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdatper;
import vincemann.github.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import vincemann.github.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.JSONMediaTypeStrategy;
import vincemann.github.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import vincemann.github.generic.crud.lib.demo.dtos.PetTypeDTO;
import vincemann.github.generic.crud.lib.demo.model.PetType;
import vincemann.github.generic.crud.lib.demo.service.PetTypeService;
import org.springframework.stereotype.Controller;
import vincemann.github.generic.crud.lib.dtoMapper.BasicDTOMapper;
import vincemann.github.generic.crud.lib.dtoMapper.DTOMapper;
import vincemann.github.generic.crud.lib.service.EndpointService;

@Controller
public class PetTypeController extends DTOCrudControllerSpringAdatper<PetType, PetTypeDTO,Long, PetTypeService> {

    public PetTypeController(PetTypeService crudService, EndpointService endpointService, IdFetchingStrategy<Long> longIdFetchingStrategy) {
        super(crudService,
                endpointService,
                PetType.class,
                PetTypeDTO.class,
                longIdFetchingStrategy,
                new JSONMediaTypeStrategy(),
                new JavaXValidationStrategy<>());
    }

    @Override
    protected DTOMapper<PetType, PetTypeDTO, Long> provideServiceEntityToDTOMapper() {
        return new BasicDTOMapper<>(PetTypeDTO.class);
    }

    @Override
    protected DTOMapper<PetTypeDTO, PetType, Long> provideDTOToServiceEntityMapper() {
        return new BasicDTOMapper<>(PetType.class);
    }
}
