package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.FullOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.VetReadsOwnerDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class OwnerController extends SecuredCrudController<Owner, Long, OwnerService> {


    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(roles(AuthRoles.ADMIN))
                        .thenReturn(FullOwnerDto.class);

        builder.when(endpoint(getCreateUrl()).and(direction(Direction.REQUEST)))
                .thenReturn(CreateOwnerDto.class);

        builder.when(endpoint(getCreateUrl()).and(direction(Direction.RESPONSE)))
                .thenReturn(FullOwnerDto.class);

        builder.when(endpoint(getUpdateUrl()).and(roles(MyRoles.OWNER)).and(principal(Principal.OWN)))
                .thenReturn(UpdateOwnerDto.class);

        builder.when(roles(MyRoles.OWNER).and(principal(Principal.OWN)).and(direction(Direction.RESPONSE)))
                .thenReturn(FullOwnerDto.class);

        builder.when(roles(MyRoles.VET).and(direction(Direction.RESPONSE)))
                .thenReturn(VetReadsOwnerDto.class);


    }

}
