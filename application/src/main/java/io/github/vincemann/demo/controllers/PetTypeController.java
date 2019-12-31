package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.PetTypeDto;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.repositories.PetTypeRepository;
import io.github.vincemann.demo.service.PetTypeService;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoCrudController_SpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.EndpointsExposureDetails;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import org.springframework.stereotype.Controller;

@Controller
public class PetTypeController extends DtoCrudController_SpringAdapter<PetType, PetTypeDto,Long, PetTypeRepository, PetTypeService> {

}
