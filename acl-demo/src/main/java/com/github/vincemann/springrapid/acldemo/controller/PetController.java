package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.pet.FullPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerCreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerUpdatesOwnPetDto;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;
import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.slicing.WebController;


@WebController
public class PetController extends CrudController<Pet, Long, PetService> {

    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder

                .withRoles(MyRoles.OWNER)
                .withAllPrincipals()
                .forEndpoint(getCreateUrl(),Direction.REQUEST, OwnerCreatePetDto.class)
                .forEndpoint(getCreateUrl(),Direction.RESPONSE, FullPetDto.class)


                .withRoles(MyRoles.VET)
                .withAllPrincipals()
                .forEndpoint(getUpdateUrl(), Direction.REQUEST, FullPetDto.class)

                .withRoles(MyRoles.OWNER)
                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .forEndpoint(getUpdateUrl(), Direction.REQUEST, OwnerUpdatesOwnPetDto.class)

                .withRoles(Roles.ADMIN)
                .withAllPrincipals()
                .forAll(FullPetDto.class)

                .build();
    }
}
