package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.dto.vet.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.SignupVetDto;
import com.github.vincemann.springrapid.acldemo.dto.vet.UpdateVetDto;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.acldemo.service.user.VetSignupService;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.service.SignupService;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.Lists;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class VetController extends AbstractUserController<Vet, Long, VetService> {

    private VetSignupService signupService;
    @Getter
    private String signupUrl;
    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(endpoint(getUpdateUrl())
                        .and(roles(MyRoles.VET))
                        .and(principal(Principal.OWN))
                        .and(direction(Direction.REQUEST)))
                .thenReturn(UpdateVetDto.class);


        builder.when(direction(Direction.RESPONSE)
                        .and(principal(Principal.OWN)))
                .thenReturn(ReadVetDto.class);
    }

    @Override
    protected void initUrls() {
        super.initUrls();
        this.signupUrl = "/api/core/vet/signup";
    }

    @PostMapping("/api/core/vet/signup")
    public ResponseEntity<ReadVetDto> signup(@Valid @RequestBody SignupVetDto dto) throws BadEntityException {
        Vet vet = signupService.signup(dto);
        return ResponseEntity.ok(getDtoMapper().mapToDto(vet, ReadVetDto.class));
    }

    @Autowired
    public void setSignupService(VetSignupService signupService) {
        this.signupService = signupService;
    }

    @Override
    public List<String> getIgnoredEndPoints() {
        return Lists.newArrayList(getCreateUrl(),getSignupUrl());
    }

    @Autowired
    @Secured
    @Override
    public void setCrudService(VetService crudService) {
        super.setCrudService(crudService);
    }

    @Autowired
    @Root
    @Override
    public void setUnsecuredService(VetService Service) {
        super.setUnsecuredService(Service);
    }


    // not needed, dont autowire
    @Override
    public void setSignupService(SignupService signupService) {
        super.setSignupService(signupService);
    }
}
