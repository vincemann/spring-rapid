package com.github.vincemann.springrapid.acldemo.controller.map;

import com.github.vincemann.springrapid.acldemo.dto.vet.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.model.Specialty;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class VetMappingService {

    @Transactional
    public ReadVetDto map(Vet vet){
        ReadVetDto dto = new ModelMapper().map(vet, ReadVetDto.class);
        dto.setSpecialtyIds(vet.getSpecialtys().stream().map(Specialty::getId).collect(Collectors.toSet()));
        return dto;
    }

}
