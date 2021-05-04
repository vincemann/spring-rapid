package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;
import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.acldemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.VetReadsOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.FullOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;

@WebController
public class OwnerController extends SecuredCrudController<Owner, Long, OwnerService> {


    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder

                .withAllPrincipals()
                .withAllRoles()
                .forEndpoint(getCreateUrl(),Direction.REQUEST, CreateOwnerDto.class)
                .forEndpoint(getCreateUrl(), Direction.RESPONSE, FullOwnerDto.class)


                .withRoles(MyRoles.OWNER)
                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .forUpdate(UpdateOwnerDto.class)


                .withRoles(MyRoles.OWNER)
                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .forResponse(FullOwnerDto.class)


                .withRoles(MyRoles.VET)
                .withAllPrincipals()
                .forResponse(VetReadsOwnerDto.class)


                .withRoles(Roles.ADMIN)
                .withAllPrincipals()
                .forAll(FullOwnerDto.class)
                .build();
    }

}
