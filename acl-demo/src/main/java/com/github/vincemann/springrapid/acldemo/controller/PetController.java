package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.pet.FullPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerCreatesPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerUpdatesPetDto;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;


@Controller
public class PetController extends SecuredCrudController<Pet, Long> {

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {

        builder.when(roles(AuthRoles.ADMIN))
                        .thenReturn(FullPetDto.class);

        builder.when(endpoint(getCreateUrl())
                        .and(roles(MyRoles.OWNER))
                        .and(direction(Direction.REQUEST)))
                .thenReturn(OwnerCreatesPetDto.class);

        builder.when(endpoint(getCreateUrl())
                        .and(roles(MyRoles.OWNER))
                        .and(direction(Direction.RESPONSE)))
                .thenReturn(FullPetDto.class);


        builder.when(endpoint(getUpdateUrl())
                        .and(roles(MyRoles.OWNER))
                        .and(direction(Direction.REQUEST)))
                .thenReturn(OwnerUpdatesPetDto.class);

        builder.when(roles(MyRoles.OWNER)
                        .and(direction(Direction.RESPONSE)))
                .thenReturn(FullPetDto.class);

        builder.when(endpoint(getUpdateUrl())
                        .and(roles(MyRoles.VET))
                        .and(direction(Direction.REQUEST)))
                .thenReturn(FullPetDto.class);

        builder.when(roles(MyRoles.VET)
                        .and(direction(Direction.RESPONSE)))
                .thenReturn(FullPetDto.class);

    }
}
