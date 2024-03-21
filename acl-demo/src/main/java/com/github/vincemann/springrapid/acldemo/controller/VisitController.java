package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.controller.map.VisitMappingService;
import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.ReadVisitDto;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/core/visit/")
public class VisitController
{

    private VisitService service;
    private VisitMappingService mappingService;

    @PostMapping("create")
    public ReadVisitDto create(@RequestBody CreateVisitDto dto) throws EntityNotFoundException, BadEntityException {
        Visit visit = service.create(dto);
        return mappingService.map(visit);
    }

    @RequestMapping(value = "/api/core/visit/add-spectator", method = RequestMethod.GET)
    public ResponseEntity<?> addSpectator(@RequestParam("spectator") long spectatorId, @RequestParam("visit") long visitId) throws EntityNotFoundException {
        service.addSpectator(spectatorId,visitId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/api/core/visit/remove-spectator", method = RequestMethod.GET)
    public ResponseEntity<?> removeSpectator(@RequestParam("spectator") long spectatorId, @RequestParam("visit") long visitId) throws EntityNotFoundException {
        service.removeSpectator(spectatorId,visitId);
        return ResponseEntity.ok().build();
    }

    @Autowired
    public void setMappingService(VisitMappingService mappingService) {
        this.mappingService = mappingService;
    }

    @Autowired
    @Secured
    public void setService(VisitService service) {
        this.service = service;
    }
}
