package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.ReadVisitDto;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class VisitController extends SecuredCrudController<Visit, Long, VisitService>
{
    @Getter
    private String addSpectatorUrl;
    @Getter
    private String removeSpectatorUrl;

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(endpoint(getCreateUrl()).and(direction(Direction.REQUEST)))
                        .thenReturn(CreateVisitDto.class);

        builder.when(direction(Direction.RESPONSE))
                .thenReturn(ReadVisitDto.class);
    }

    @Override
    protected void initUrls() {
        super.initUrls();
        this.addSpectatorUrl = "/api/core/visit/add-spectator";
        this.removeSpectatorUrl = "/api/core/visit/remove-spectator";
    }

    @RequestMapping(value = "/api/core/visit/add-spectator", method = RequestMethod.GET)
    public ResponseEntity<?> addSpectator(@RequestParam("spectatorId") long spectatorId, @RequestParam("visit") long visitId) throws EntityNotFoundException {
        getService().addSpectator(spectatorId,visitId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/api/core/visit/add-spectator", method = RequestMethod.GET)
    public ResponseEntity<?> removeSpectator(@RequestParam("spectatorId") long spectatorId, @RequestParam("visit") long visitId) throws EntityNotFoundException {
        getService().removeSpectator(spectatorId,visitId);
        return ResponseEntity.ok().build();
    }

}
