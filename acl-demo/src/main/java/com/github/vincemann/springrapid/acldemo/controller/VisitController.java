package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.core.controller.dto.map.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappings;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import com.github.vincemann.springrapid.acldemo.dto.VisitDto;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class VisitController
        extends SecuredCrudController<Visit, Long, VisitService>
{
    @Override
    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder.forAll(VisitDto.class).build();
    }

    private OwnerService ownerService;
    @Getter
    private String subscribeOwnerUrl;


    @Override
    protected void initUrls() {
        super.initUrls();
        this.subscribeOwnerUrl = getEntityBaseUrl() + "subscribe-owner";
    }

    @RequestMapping(value = "/api/core/visit/subscribe-owner", method = RequestMethod.GET)
    public ResponseEntity<?> subscribeOwner(@RequestParam(value = "read") boolean canRead, @RequestParam("ownerid") long ownerId, @RequestParam("visitid") long visitId) throws BadEntityException, EntityNotFoundException {
        Optional<Owner> owner = ownerService.findById(ownerId);
        Optional<Visit> visit = getService().findById(visitId);
        VerifyEntity.isPresent(owner,"Owner with id: " + ownerId + " not found");
        VerifyEntity.isPresent(visit,"Visit with id: " + visitId + " not found");
        if (canRead){
            getService().subscribeOwner(owner.get(),visit.get());
        }else {
            getService().unsubscribeOwner(owner.get(),visit.get());
        }
        return ResponseEntity.ok().build();
    }


    @Autowired
    public void injectOwnerService(OwnerService ownerService) {
        this.ownerService = ownerService;
    }
}
