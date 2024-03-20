package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.controller.FetchableEntityController;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import com.github.vincemann.springrapid.syncdemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.service.OwnerService;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class OwnerController extends FetchableEntityController<Owner, Long, OwnerService> {

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(endpoint(getCreateUrl()).and(direction(Direction.REQUEST)))
                        .thenReturn(CreateOwnerDto.class);

        builder.when(endpoint(getCreateUrl()).and(direction(Direction.RESPONSE)))
                .thenReturn(ReadOwnOwnerDto.class);

        builder.when(endpoint(getUpdateUrl()).and(direction(Direction.REQUEST)))
                .thenReturn(CreateOwnerDto.class);

        builder.when(direction(Direction.RESPONSE).and(principal(Principal.OWN)))
                        .thenReturn(ReadOwnOwnerDto.class);

        builder.when(direction(Direction.RESPONSE).and(principal(Principal.FOREIGN)))
                .thenReturn(ReadForeignOwnerDto.class);
    }

//    @Override
//    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
//        return builder
//
//                .forEndpoint(getCreateUrl(),CreateOwnerDto.class)
//                .forUpdate(UpdateOwnerDto.class)
//                //response dto config
//                //authenticated
//                .withPrincipal(DtoRequestInfo.Principal.OWN)
//                .forResponse(ReadOwnOwnerDto.class)
//                .withPrincipal(DtoRequestInfo.Principal.FOREIGN)
//                .forResponse(ReadForeignOwnerDto.class)
//                //not authenticated
//                .withAllPrincipals()
//                .forResponse(ReadForeignOwnerDto.class)
//
//                // if you can update or create you can read all of the data
//                .forEndpoint(getCreateUrl(), Direction.RESPONSE,ReadOwnOwnerDto.class)
//                .forEndpoint(getUpdateUrl(),Direction.RESPONSE,ReadOwnOwnerDto.class)
//
//                .build();
//    }

}
