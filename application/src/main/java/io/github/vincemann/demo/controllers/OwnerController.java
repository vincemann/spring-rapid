package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.owner.CreateOwnerDto;
import io.github.vincemann.demo.dtos.owner.ReadOwnerDto;
import io.github.vincemann.demo.dtos.owner.UpdateOwnerDto;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.generic.crud.lib.config.layers.component.WebController;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.context.Direction;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.context.CrudDtoEndpoint;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterJsonDtoCrudController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@WebController
public class OwnerController
        extends SpringAdapterJsonDtoCrudController<Owner, Long> {


    public OwnerController() {
        super(
                DtoMappingContextBuilder.builder()
                        .forEndpoint(CrudDtoEndpoint.CREATE,CreateOwnerDto.class)
                        .forUpdate(Direction.REQUEST,UpdateOwnerDto.class)
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
