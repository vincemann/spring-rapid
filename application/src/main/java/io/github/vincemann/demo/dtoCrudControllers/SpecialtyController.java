package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdatper;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.JSONMediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import io.github.vincemann.demo.dtos.SpecialtyDTO;
import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.service.SpecialtyService;
import org.springframework.stereotype.Controller;
import io.github.vincemann.generic.crud.lib.dtoMapper.BasicDTOMapper;
import io.github.vincemann.generic.crud.lib.dtoMapper.DTOMapper;
import io.github.vincemann.generic.crud.lib.service.EndpointService;

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
