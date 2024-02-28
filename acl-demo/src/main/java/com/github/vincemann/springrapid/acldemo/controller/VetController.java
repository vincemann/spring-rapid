package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.SignupOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.CreateVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.FullVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.VetUpdatesOwnDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.acldemo.service.user.VetSignupService;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import java.util.List;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class VetController extends SecuredCrudController<Vet, Long, VetService> {

    private VetSignupService signupService;
    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(roles(AuthRoles.ADMIN))
                        .thenReturn(FullVetDto.class);

        builder.when(endpoint(getCreateUrl())
                        .and(direction(Direction.REQUEST)))
                .thenReturn(CreateVetDto.class);

        builder.when(endpoint(getUpdateUrl())
                        .and(roles(MyRoles.VET))
                        .and(principal(Principal.OWN))
                        .and(direction(Direction.REQUEST)))
                .thenReturn(VetUpdatesOwnDto.class);


        builder.when(direction(Direction.RESPONSE)
                        .and(principal(Principal.OWN)))
                .thenReturn(FullVetDto.class);
    }

    @PostMapping("/api/core/vet/signup")
    public ResponseEntity<FullVetDto> signup(@Valid @RequestBody SignupVetDto dto) throws BadEntityException {
        Vet vet = signupService.signup(dto);
        return ResponseEntity.ok(getDtoMapper().mapToDto(vet, FullVetDto.class));
    }

    @Autowired
    public void setSignupService(VetSignupService signupService) {
        this.signupService = signupService;
    }

    @Override
    public List<String> getIgnoredEndPoints() {
        return Lists.newArrayList(getCreateUrl());
    }

}
