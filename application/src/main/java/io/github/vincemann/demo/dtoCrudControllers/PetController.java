package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.demo.dtos.PetDto;
import io.github.vincemann.demo.plugins.AclPlugin;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.EndpointsExposureDetails;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import org.springframework.stereotype.Controller;

import javax.validation.ConstraintViolationException;

@Controller
public class PetController extends DtoCrudControllerSpringAdapter<Pet, PetDto,Long, PetService> {


    public PetController(PetService crudService,
                         IdFetchingStrategy<Long> longIdFetchingStrategy,
                         MediaTypeStrategy mediaTypeStrategy,
                         ValidationStrategy validationStrategy,
                         DtoMapper dtoMapper,
                         EndpointsExposureDetails endpointsExposureDetails,
                         EndpointService endpointService) {
        super(
                crudService,
                endpointService,
                longIdFetchingStrategy,
                mediaTypeStrategy,
                validationStrategy,
                dtoMapper,
                endpointsExposureDetails,
                //only needs AclPlugin
                new AclPlugin());
    }

}
