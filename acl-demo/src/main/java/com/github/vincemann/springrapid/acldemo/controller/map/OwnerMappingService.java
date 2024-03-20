package com.github.vincemann.springrapid.acldemo.controller.map;

import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.dto.owner.OwnerReadsForeignOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.OwnerReadsOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.VetReadsOwnerDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class OwnerMappingService {


    @Transactional
    public Object mapToReadOwnerDto(Owner owner){
        if (RapidSecurityContext.getRoles().contains(Roles.VET)){
            return mapToVetReadsOwnerDto(owner);
        }
        else if (RapidSecurityContext.getRoles().contains(Roles.OWNER)){
            if (owner.getLastName().equals(RapidSecurityContext.getName())){
                return mapToReadOwnOwner(owner);
            }
            else{
                return mapToOwnerReadsForeignOwnerDto(owner);
            }
        }
        throw new IllegalArgumentException("cant find target dto class for reading owner");
    }


    @Transactional
    public OwnerReadsOwnOwnerDto mapToReadOwnOwner(Owner owner){
        OwnerReadsOwnOwnerDto dto = new ModelMapper().map(owner, OwnerReadsOwnOwnerDto.class);
        dto.setPetIds(owner.getPets().stream().map(Pet::getId).collect(Collectors.toSet()));
        dto.setSecret(Owner.SECRET);
        return dto;
    }

    @Transactional
    public VetReadsOwnerDto mapToVetReadsOwnerDto(Owner owner){
        VetReadsOwnerDto dto = new ModelMapper().map(owner, VetReadsOwnerDto.class);
        dto.setPetIds(owner.getPets().stream().map(Pet::getId).collect(Collectors.toSet()));
        return dto;
    }

    @Transactional
    public Object mapToOwnerReadsForeignOwnerDto(Owner owner) {
        OwnerReadsForeignOwnerDto dto = new ModelMapper().map(owner, OwnerReadsForeignOwnerDto.class);
        dto.setPetIds(owner.getPets().stream().map(Pet::getId).collect(Collectors.toSet()));
        return dto;
    }
}
