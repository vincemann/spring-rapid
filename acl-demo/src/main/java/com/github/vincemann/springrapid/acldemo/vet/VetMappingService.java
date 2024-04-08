package com.github.vincemann.springrapid.acldemo.vet;

import com.github.vincemann.springrapid.acldemo.vet.dto.ReadVetDto;
import com.github.vincemann.springrapid.acldemo.Specialty;
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
