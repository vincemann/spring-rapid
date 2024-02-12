package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.vet.CreateVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.FullVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.VetUpdatesOwnDto;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class VetController
        extends SecuredCrudController<Vet, Long, VetService> {

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(roles(AuthRoles.ADMIN))
                        .thenReturn(FullVetDto.class);

        builder.when(endpoint(getCreateUrl()).and(direction(Direction.REQUEST)))
                .thenReturn(CreateVetDto.class);

        builder.when(endpoint(getUpdateUrl())
                        .and(roles(MyRoles.VET))
                        .and(principal(Principal.OWN))
                        .and(direction(Direction.REQUEST)))
                .thenReturn(VetUpdatesOwnDto.class);


        builder.when(direction(Direction.RESPONSE))
                .thenReturn(FullVetDto.class);


    }
}
