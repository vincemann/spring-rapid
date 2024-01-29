package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.pet.FullPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerCreatesPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerUpdatesPetDto;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.core.controller.dto.map.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappings;
import com.github.vincemann.springrapid.core.sec.Roles;
import org.springframework.stereotype.Controller;


@Controller
public class PetController extends SecuredCrudController<Pet, Long, PetService> {

    @Override
    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
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
