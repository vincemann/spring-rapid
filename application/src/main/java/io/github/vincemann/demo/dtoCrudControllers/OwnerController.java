package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdatper;
import io.github.vincemann.generic.crud.lib.dtoMapper.BasicDTOMapper;
import io.github.vincemann.generic.crud.lib.dtoMapper.DTOMapper;
import io.github.vincemann.generic.crud.lib.dtoMapper.backRefResolving.BackRefResolvingConverter;
import io.github.vincemann.generic.crud.lib.dtoMapper.backRefResolving.BackRefResolvingDTOMapper;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import io.github.vincemann.demo.dtos.OwnerDTO;
import io.github.vincemann.demo.dtos.PetDTO;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.OwnerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import io.github.vincemann.generic.crud.lib.service.EndpointService;

import java.util.Collections;

@Controller
public class OwnerController extends DTOCrudControllerSpringAdatper<Owner, OwnerDTO,Long,OwnerService> {

    @Autowired
    public OwnerController(OwnerService crudService, EndpointService endpointService, MediaTypeStrategy mediaTypeStrategy, IdFetchingStrategy<Long> longIdFetchingStrategy){
        super(
                crudService,
                endpointService,
                Owner.class,
                OwnerDTO.class,
                longIdFetchingStrategy,
                mediaTypeStrategy,
                new JavaXValidationStrategy<>()
        );
    }

    @Override
    protected DTOMapper<Owner, OwnerDTO, Long> provideServiceEntityToDTOMapper() {
        //das kann der auch tatsächlich anhand des property names selber mappen, aber ich mach das lieber explizit
        ModelMapper modelMapper = new ModelMapper();
        //map backref to id
        modelMapper.createTypeMap(Pet.class, PetDTO.class)
                .<Long>addMapping(pet -> pet.getOwner().getId(), PetDTO::setOwnerId);
        return new BasicDTOMapper<>(OwnerDTO.class,modelMapper);
    }


    @Override
    protected DTOMapper<OwnerDTO, Owner, Long> provideDTOToServiceEntityMapper() {
        /*ModelMapper dtoMapper = new ModelMapper();
        dtoMapper.addConverter(new Converter<PetDTO, Pet>() {

            @Override
            public Pet convert(MappingContext<PetDTO, Pet> context) {
                Owner owner;
                Long ownerId = context.getSource().getOwnerId();
                if(ownerId!=null) {
                    try {
                        Optional<Owner> optionalOwner = getCrudService().findById(ownerId);
                        if (optionalOwner.isPresent()) {
                            owner = optionalOwner.get();
                        } else {
                            throw new RuntimeException(new EntityNotFoundException("No Owner found with id: " + ownerId));
                        }

                    } catch (NoIdException e) {
                        throw new RuntimeException(e);
                    }
                    Pet mappedPet = new ModelMapper().map(context.getSource(),context.getDestinationType());
                    mappedPet.setOwner(owner);
                    return mappedPet;
                }else {
                    Pet mappedPet = new ModelMapper().map(context.getSource(),context.getDestinationType());
                    mappedPet.setOwner(null);
                    return mappedPet;
                }
            }
        });
        return new BasicDTOMapper<>(Owner.class,dtoMapper);*/
        @SuppressWarnings("unchecked")
        BackRefResolvingConverter<PetDTO,Pet,Owner,Long,Long> petDTOPetBackRefResolvingConverter = new BackRefResolvingConverter(getCrudService(),Owner.class,PetDTO.class,Pet.class);
        return new BackRefResolvingDTOMapper<OwnerDTO,Owner,Long>(Owner.class,Collections.singletonList(petDTOPetBackRefResolvingConverter));
    }

    /*@Override
    protected Owner beforeCreateEntity(Owner owner, OwnerDTO ownerDTO) {
        //manuell die backref setzen für pets von owner, wenn diese nicht gesetzt ist
        for(Pet pet: owner.getPets()){
            if(pet.getOwner()==null) {
                pet.setOwner(owner);
            }
        }
        return super.beforeCreateEntity(owner, ownerDTO);
    }*/

    @RequestMapping("/owners")
    public String listOwners(Model model){
        model.addAttribute("owners",getCrudService().findAll());
        return "owners/index";
    }
}
