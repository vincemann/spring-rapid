package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.EndpointsExposureDetails;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.demo.dtos.PetTypeDto;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.service.PetTypeService;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import org.springframework.stereotype.Controller;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;

@Controller
public class PetTypeController extends DTOCrudControllerSpringAdapter<PetType, PetTypeDto,Long, PetTypeService> {

    public PetTypeController(PetTypeService crudService, IdFetchingStrategy<Long> longIdFetchingStrategy, MediaTypeStrategy mediaTypeStrategy, ValidationStrategy validationStrategy, DtoMapper dtoMapper, EndpointService endpointService) {
        super(crudService,
                endpointService, longIdFetchingStrategy,
                mediaTypeStrategy,
                validationStrategy,
                dtoMapper,
                //no update Endpoint
                EndpointsExposureDetails.builder()
                        .updateEndpointExposed(false)
                        .build());
    }

}
