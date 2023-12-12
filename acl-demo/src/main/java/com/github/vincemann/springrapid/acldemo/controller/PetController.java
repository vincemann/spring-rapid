package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.pet.FullPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerCreatesPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerUpdatesPetDto;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;
import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.slicing.WebController;


@WebController
public class PetController extends SecuredCrudController<Pet, Long, PetService> {

    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder

                .withRoles(MyRoles.OWNER)
                .withAllPrincipals()
                .forEndpoint(getCreateUrl(),Direction.REQUEST, OwnerCreatesPetDto.class)
                .forEndpoint(getCreateUrl(),Direction.RESPONSE, FullPetDto.class)


                .withRoles(MyRoles.OWNER)
//                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .withAllPrincipals()
                .forEndpoint(getUpdateUrl(), Direction.REQUEST, OwnerUpdatesPetDto.class)

                .withRoles(MyRoles.OWNER)
//                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .withAllPrincipals()
                .forResponse(FullPetDto.class)

                .withRoles(MyRoles.VET)
                .withAllPrincipals()
                .forEndpoint(getUpdateUrl(), Direction.REQUEST, FullPetDto.class)
                .forResponse(FullPetDto.class)



                .withRoles(Roles.ADMIN)
                .withAllPrincipals()
                .forAll(FullPetDto.class)

                .build();
    }
}
