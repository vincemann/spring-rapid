package com.github.vincemann.springrapid.acldemo.visit;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.visit.dto.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.visit.dto.ReadVisitDto;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import com.github.vincemann.springrapid.auth.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/api/core/visit/")
public class VisitController
{

    private VisitService service;
    private VisitMappingService mappingService;

    @GetMapping("find")
    public ResponseEntity<ReadVisitDto> find(@RequestParam("id") long id) throws EntityNotFoundException {
        Optional<Visit> visit = service.find(id);
        VerifyEntity.isPresent(visit,id,Visit.class);
        return ResponseEntity.ok(mappingService.map(visit.get()));
    }

    @PostMapping("create")
    public ResponseEntity<ReadVisitDto> create(@RequestBody CreateVisitDto dto) throws EntityNotFoundException, BadEntityException {
        Visit visit = service.create(dto);
        return ResponseEntity.ok(mappingService.map(visit));
    }

    @PutMapping(value = "add-spectator")
    public ResponseEntity<?> addSpectator(@RequestParam("spectator") long spectatorId, @RequestParam("visit") long visitId) throws EntityNotFoundException {
        service.addSpectator(spectatorId,visitId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "remove-spectator")
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
