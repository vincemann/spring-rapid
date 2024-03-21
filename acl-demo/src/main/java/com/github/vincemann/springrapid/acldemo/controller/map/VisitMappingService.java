package com.github.vincemann.springrapid.acldemo.controller.map;

import com.github.vincemann.springrapid.acldemo.dto.visit.ReadVisitDto;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class VisitMappingService {

    @Transactional
    public ReadVisitDto map(Visit visit){
        ReadVisitDto dto = new ModelMapper().map(visit, ReadVisitDto.class);
        dto.setOwnerId(visit.getOwner() == null ? null : visit.getOwner().getId());
        dto.setVetId(visit.getVet() == null ? null : visit.getVet().getId());
        dto.setPetIds(visit.getPets().stream().map(Pet::getId).collect(Collectors.toSet()));
        return dto;
    }
}
