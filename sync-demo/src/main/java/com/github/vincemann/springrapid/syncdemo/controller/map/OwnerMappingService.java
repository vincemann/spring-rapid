package com.github.vincemann.springrapid.syncdemo.controller.map;

import com.github.vincemann.springrapid.syncdemo.dto.owner.ReadOwnerDto;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class OwnerMappingService {

    @Transactional
    public ReadOwnerDto map(Owner owner){
        ReadOwnerDto dto = new ModelMapper().map(owner, ReadOwnerDto.class);
        dto.setClinicCardId(owner.getClinicCard() == null ? null : owner.getClinicCard().getId());
        dto.setDirtySecret(Owner.SECRET);
        dto.setPetIds(owner.getPets().stream().map(Pet::getId).collect(Collectors.toSet()));
        return dto;
    }
}
