package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.demo.dtos.PetDto;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import org.springframework.stereotype.Controller;

@Controller
public class PetController extends DTOCrudControllerSpringAdapter<Pet, PetDto,Long, PetService> {

    private OwnerService ownerService;

    public PetController(PetService crudService, IdFetchingStrategy<Long> longIdFetchingStrategy, MediaTypeStrategy mediaTypeStrategy, OwnerService ownerService, ValidationStrategy validationStrategy, DtoMapper dtoMapper, EndpointService endpointService) {
        super(
                crudService,
                longIdFetchingStrategy,
                mediaTypeStrategy,
                validationStrategy,
                dtoMapper,
                endpointService
                );
        this.ownerService=ownerService;
    }
}
