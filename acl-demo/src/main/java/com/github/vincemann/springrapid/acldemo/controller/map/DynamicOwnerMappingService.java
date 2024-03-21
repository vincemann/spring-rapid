package com.github.vincemann.springrapid.acldemo.controller.map;

import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamicOwnerMappingService {

    private final OwnerMappingService ownerMappingService;

    @Autowired
    public DynamicOwnerMappingService(OwnerMappingService ownerMappingService) {
        this.ownerMappingService = ownerMappingService;
    }

    public Object mapOwnerBasedOnRole(Owner owner) {
        if (RapidSecurityContext.getRoles().contains(Roles.VET)) {
            return ownerMappingService.mapToVetReadsOwnerDto(owner);
        } else if (RapidSecurityContext.getRoles().contains(Roles.OWNER)) {
            if (owner.getLastName().equals(RapidSecurityContext.getName())) {
                return ownerMappingService.mapToReadOwnOwner(owner);
            } else {
                return ownerMappingService.mapToOwnerReadsForeignOwnerDto(owner);
            }
        }
        throw new IllegalArgumentException("Cannot find target DTO class for reading owner.");
    }
}

