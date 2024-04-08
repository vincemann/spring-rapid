package com.github.vincemann.springrapid.acldemo.controller.map;

import com.github.vincemann.springrapid.acldemo.dto.owner.OwnerReadsForeignOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.OwnerReadsOwnOwnerDto;
import com.github.vincemann.springrapid.acldemo.dto.owner.VetReadsOwnerDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class OwnerMappingService {
    


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
        return new ModelMapper().map(owner, OwnerReadsForeignOwnerDto.class);
    }
}
