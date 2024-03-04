package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.acldemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.VetReadsOwnerDto;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.user.OwnerSignupService;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.SignupService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class OwnerController extends AbstractUserController<com.github.vincemann.springrapid.acldemo.model.Owner, Long, OwnerService> {

    @Getter
    private String signupUrl;
    private OwnerSignupService signupService;

    @Override
    protected void initUrls() {
        super.initUrls();
        this.signupUrl = "/api/core/owner/signup";
    }



    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(roles(AuthRoles.ADMIN))
                        .thenReturn(ReadOwnOwnerDto.class);

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

    @RequestMapping(value = "/api/core/visit/permit-owner-read-pets", method = RequestMethod.GET)
    public ResponseEntity<?> permitOwnerReadPets(@RequestParam("permittedOwnerId") long permittedOwnerId, @RequestParam("targetOwnerId") long targetOwnerId) throws EntityNotFoundException {
        getService().permitOwnerReadPets(permittedOwnerId, targetOwnerId);
        return ResponseEntity.ok().build();
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

    @Autowired
    @Root
    @Override
    public void setUnsecuredService(OwnerService Service) {
        super.setUnsecuredService(Service);
    }


    // not needed, dont autowire
    @Override
    public void setSignupService(SignupService signupService) {
        super.setSignupService(signupService);
    }

}
