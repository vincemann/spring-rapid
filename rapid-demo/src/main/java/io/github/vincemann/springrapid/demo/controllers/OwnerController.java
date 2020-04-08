package io.github.vincemann.springrapid.demo.controllers;

import io.github.vincemann.springrapid.demo.dtos.owner.CreateOwnerDto;
import io.github.vincemann.springrapid.demo.dtos.owner.ReadOwnerDto;
import io.github.vincemann.springrapid.demo.dtos.owner.UpdateOwnerDto;
import io.github.vincemann.springrapid.demo.model.Owner;
import io.github.vincemann.springrapid.core.slicing.components.WebController;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.CrudDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@WebController
public class OwnerController
        extends RapidController<Owner, Long> {


    public OwnerController() {
        super(
                DtoMappingContextBuilder.builder()
                        .forEndpoint(CrudDtoEndpoint.CREATE, CreateOwnerDto.class)
                        .forUpdate(Direction.REQUEST, UpdateOwnerDto.class)
                        .forResponse(ReadOwnerDto.class)
                        .build()
        );

    }

    @RequestMapping("/owners")
    public String listOwners(Model model) {
        model.addAttribute("owners", getCrudService().findAll());
        return "owners/index";
    }

}
