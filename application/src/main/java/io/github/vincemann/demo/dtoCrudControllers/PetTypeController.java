package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.JSONMediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import io.github.vincemann.demo.dtos.PetTypeDTO;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.service.PetTypeService;
import org.springframework.stereotype.Controller;
import io.github.vincemann.generic.crud.lib.dtoMapper.BasicDTOMapper;
import io.github.vincemann.generic.crud.lib.dtoMapper.DTOMapper;
import io.github.vincemann.generic.crud.lib.service.EndpointService;

@Controller
public class PetTypeController extends DTOCrudControllerSpringAdapter<PetType, PetTypeDTO,Long, PetTypeService> {

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
