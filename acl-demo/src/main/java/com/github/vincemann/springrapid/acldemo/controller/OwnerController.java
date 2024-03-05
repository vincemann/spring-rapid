package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.VetReadsOwnerDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.user.OwnerSignupService;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.Lists;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class OwnerController extends SecuredCrudController<Owner, Long, OwnerService> {

    @Getter
    private String signupUrl;
    @Getter
    private String addPetSpectatorUrl;
    private OwnerSignupService signupService;

    @Override
    protected void initUrls() {
        super.initUrls();
        this.signupUrl = "/api/core/owner/signup";
        this.addPetSpectatorUrl = "/api/core/owner/add-pet-spectator";
    }

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(endpoint(getUpdateUrl()).and(roles(MyRoles.OWNER)).and(principal(Principal.OWN)))
                .thenReturn(UpdateOwnerDto.class);

        builder.when(roles(MyRoles.OWNER).and(principal(Principal.OWN)).and(direction(Direction.RESPONSE)))
                .thenReturn(ReadOwnOwnerDto.class);

        builder.when(roles(MyRoles.VET).and(direction(Direction.RESPONSE)))
                .thenReturn(VetReadsOwnerDto.class);
    }

    @PostMapping("/api/core/owner/signup")
    public ResponseEntity<ReadOwnOwnerDto> signup(@Valid @RequestBody SignupOwnerDto dto) throws BadEntityException {
        com.github.vincemann.springrapid.acldemo.model.Owner owner = signupService.signup(dto);
        return ResponseEntity.ok(getDtoMapper().mapToDto(owner, ReadOwnOwnerDto.class));
    }

    @GetMapping(value = "/api/core/owner/add-pet-spectator")
    public ResponseEntity<?> addPetSpectator(@RequestParam("permitted") long permittedOwnerId, @RequestParam("target") long targetOwnerId) throws EntityNotFoundException {
        getService().addPetSpectator(permittedOwnerId, targetOwnerId);
        return ResponseEntity.noContent().build();
    }


    @Override
    public List<String> getIgnoredEndPoints() {
        return Lists.newArrayList(getCreateUrl(),getSignupUrl());
    }



    @Autowired
    public void setSignupService(OwnerSignupService signupService) {
        this.signupService = signupService;
    }

    @Autowired
    @Secured
    @Override
    public void setCrudService(OwnerService crudService) {
        super.setCrudService(crudService);
    }

}
