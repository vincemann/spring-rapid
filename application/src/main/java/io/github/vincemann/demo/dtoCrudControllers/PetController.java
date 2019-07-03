package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.dtoMapper.BasicDTOMapper;
import io.github.vincemann.generic.crud.lib.dtoMapper.DTOMapper;
import io.github.vincemann.generic.crud.lib.dtoMapper.backRefResolving.BackRefResolvingConverter;
import io.github.vincemann.generic.crud.lib.dtoMapper.backRefResolving.BackRefResolvingDTOMapper;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import io.github.vincemann.demo.dtos.PetDTO;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import io.github.vincemann.generic.crud.lib.service.EndpointService;

import java.util.Collections;

@Controller
public class PetController extends DTOCrudControllerSpringAdapter<Pet, PetDTO,Long, PetService> {

    private OwnerService ownerService;

    public PetController(PetService crudService, EndpointService endpointService, IdFetchingStrategy<Long> longIdFetchingStrategy, MediaTypeStrategy mediaTypeStrategy, OwnerService ownerService) {
        super(
                crudService,
                endpointService,
                Pet.class,
                PetDTO.class,
                longIdFetchingStrategy,
                mediaTypeStrategy,
                new JavaXValidationStrategy<>());
        this.ownerService=ownerService;
    }

    @Override
    protected DTOMapper<Pet, PetDTO, Long> provideServiceEntityToDTOMapper() {
        //das kann der auch tatsächlich anhand des property names selber mappen, aber ich mach das lieber explizit
        ModelMapper modelMapper = new ModelMapper();
        //map backref to id
        modelMapper.createTypeMap(Pet.class,PetDTO.class)
                .<Long>addMapping(pet -> pet.getOwner().getId(), PetDTO::setOwnerId);
        return new BasicDTOMapper<>(PetDTO.class,modelMapper);
    }

    @Override
    protected DTOMapper<PetDTO, Pet, Long> provideDTOToServiceEntityMapper() {
        //todo why is this unchecked
        BackRefResolvingConverter<PetDTO,Pet,Owner,Long,Long> petDTOPetBackRefResolvingConverter = new <PetDTO,Pet,Owner,Long>BackRefResolvingConverter(ownerService, Owner.class,PetDTO.class,Pet.class);
        return new BackRefResolvingDTOMapper<PetDTO,Pet,Long>(Pet.class, Collections.singletonList(petDTOPetBackRefResolvingConverter));
    }
}
