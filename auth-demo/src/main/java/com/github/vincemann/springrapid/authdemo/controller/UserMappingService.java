package com.github.vincemann.springrapid.authdemo.controller;

import com.github.vincemann.springrapid.authdemo.dto.ReadUserDto;
import com.github.vincemann.springrapid.authdemo.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserMappingService {

    public ReadUserDto map(User user){
        ReadUserDto dto = new ModelMapper().map(user, ReadUserDto.class);
        dto.initFlags();
        return dto;
    }
}
