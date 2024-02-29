package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acl.SecuredCrudController;
import com.github.vincemann.springrapid.acldemo.dto.VisitDto;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.any;

@Controller
public class VisitController extends SecuredCrudController<Visit, Long, VisitService>
{
    @Getter
    private String subscribeOwnerUrl;

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(any()).thenReturn(VisitDto.class);
    }

    @Override
    protected void initUrls() {
        super.initUrls();
        this.subscribeOwnerUrl = getEntityBaseUrl() + "subscribe-owner";
    }

    @RequestMapping(value = "/api/core/visit/subscribe-owner", method = RequestMethod.GET)
    public ResponseEntity<?> subscribeOwner(@RequestParam(value = "subscribe") boolean subscribe, @RequestParam("owner-id") long ownerId, @RequestParam("visit-id") long visitId) throws BadEntityException, EntityNotFoundException {
        if (subscribe){
            getService().subscribeOwner(ownerId,visitId);
        }else {
            getService().unsubscribeOwner(ownerId,visitId);
        }
        return ResponseEntity.ok().build();
    }

}
