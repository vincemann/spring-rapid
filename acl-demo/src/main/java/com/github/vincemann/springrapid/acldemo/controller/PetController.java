package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.pet.*;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;


@Controller
public class PetController extends SecuredCrudController<Pet, Long, PetService> {

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {

        builder.when(endpoint(getCreateUrl())
                        .and(roles(MyRoles.OWNER))
                        .and(direction(Direction.REQUEST)))
                .thenReturn(CreatePetDto.class);

        builder.when(endpoint(getUpdateUrl())
                        .and(roles(MyRoles.OWNER))
                        .and(direction(Direction.REQUEST)))
                .thenReturn(OwnerUpdatesPetDto.class);

        builder.when(roles(MyRoles.OWNER)
                        .and(direction(Direction.RESPONSE))
                        .and(principal(Principal.OWN)))
                .thenReturn(OwnerReadsOwnPetDto.class);

        builder.when(roles(MyRoles.OWNER)
                        .and(direction(Direction.RESPONSE))
                        .and(principal(Principal.FOREIGN)))
                .thenReturn(OwnerReadsForeignPetDto.class);

        builder.when(endpoint(getUpdateUrl())
                        .and(roles(MyRoles.VET))
                        .and(direction(Direction.REQUEST)))
                .thenReturn(VetUpdatesPetDto.class);

        builder.when(roles(MyRoles.VET)
                        .and(direction(Direction.RESPONSE)))
                .thenReturn(VetReadsPetDto.class);

    }
}
